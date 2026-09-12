const fs = require('fs-extra');
const path = require('path');
const axios = require('axios');

class DownloadManager {
  constructor() {
    this.queue = [];
    this.activeDownloads = new Map();
    this.maxConcurrent = 3;
  }

  /**
   * Add a download to the queue
   * @param {object} downloadOptions - Download options
   * @returns {Promise<object>} - Download result
   */
  async addToQueue(downloadOptions) {
    const downloadItem = {
      id: Date.now().toString() + Math.random().toString(36).substr(2, 9),
      url: downloadOptions.url,
      destination: downloadOptions.destination,
      name: downloadOptions.name || path.basename(downloadOptions.destination),
      size: downloadOptions.size || 0,
      progress: 0,
      status: 'queued', // queued, downloading, completed, failed, cancelled
      startTime: null,
      endTime: null,
      error: null,
    };

    this.queue.push(downloadItem);
    this.processQueue();

    return downloadItem;
  }

  /**
   * Process the download queue
   */
  async processQueue() {
    while (this.queue.length > 0 && this.activeDownloads.size < this.maxConcurrent) {
      const item = this.queue.shift();
      if (item.status === 'queued') {
        this.startDownload(item);
      }
    }
  }

  /**
   * Start a download
   * @param {object} item - Download item
   */
  async startDownload(item) {
    item.status = 'downloading';
    item.startTime = Date.now();
    this.activeDownloads.set(item.id, item);

    try {
      const response = await axios({
        method: 'GET',
        url: item.url,
        responseType: 'stream',
        onDownloadProgress: (progressEvent) => {
          const totalLength = progressEvent.length || progressEvent.total;
          const loadedLength = progressEvent.loaded;
          
          if (totalLength) {
            item.progress = Math.round((loadedLength / totalLength) * 100);
            item.downloadedBytes = loadedLength;
            item.totalBytes = totalLength;
            
            // Emit progress event (would be handled by renderer)
            this.emitProgress(item);
          }
        },
      });

      // Ensure destination directory exists
      const destDir = path.dirname(item.destination);
      await fs.ensureDir(destDir);

      // Write the file
      const writer = fs.createWriteStream(item.destination);
      
      response.data.pipe(writer);

      await new Promise((resolve, reject) => {
        writer.on('finish', resolve);
        writer.on('error', reject);
      });

      item.status = 'completed';
      item.endTime = Date.now();
      item.progress = 100;
      
      this.emitProgress(item);
    } catch (error) {
      item.status = 'failed';
      item.error = error.message;
      item.endTime = Date.now();
      
      this.emitProgress(item);
    } finally {
      this.activeDownloads.delete(item.id);
      this.processQueue();
    }
  }

  /**
   * Cancel a download
   * @param {string} itemId - Download item ID
   */
  cancelDownload(itemId) {
    const item = this.queue.find(i => i.id === itemId);
    if (item && item.status === 'queued') {
      item.status = 'cancelled';
      this.queue = this.queue.filter(i => i.id !== itemId);
      this.emitProgress(item);
    }

    // For active downloads, we'd need to abort the axios request
    // This is a simplified implementation
  }

  /**
   * Cancel all downloads
   */
  cancelAllDownloads() {
    this.queue.forEach(item => {
      if (item.status === 'queued') {
        item.status = 'cancelled';
      }
    });
    this.queue = [];
    
    // Cancel active downloads
    this.activeDownloads.forEach(item => {
      item.status = 'cancelled';
      this.emitProgress(item);
    });
  }

  /**
   * Get queue status
   * @returns {object} - Queue status
   */
  getQueueStatus() {
    const totalItems = this.queue.length + this.activeDownloads.size;
    const completedItems = Array.from(this.activeDownloads.values())
      .filter(i => i.status === 'completed').length;
    
    const totalProgress = this.calculateTotalProgress();

    return {
      totalItems,
      activeCount: this.activeDownloads.size,
      queuedCount: this.queue.length,
      totalProgress,
    };
  }

  /**
   * Calculate total progress across all downloads
   * @returns {number} - Total progress percentage
   */
  calculateTotalProgress() {
    const allItems = [...this.queue, ...Array.from(this.activeDownloads.values())];
    
    if (allItems.length === 0) return 0;

    const totalProgress = allItems.reduce((sum, item) => sum + (item.progress || 0), 0);
    return Math.round(totalProgress / allItems.length);
  }

  /**
   * Emit progress event
   * @param {object} item - Download item
   */
  emitProgress(item) {
    // In a real implementation, this would use Electron IPC
    // to send progress updates to the renderer process
    console.log(`Download ${item.name}: ${item.progress}% (${item.status})`);
  }

  /**
   * Download a mod from Modrinth
   * @param {string} modId - Mod ID
   * @param {string} versionId - Version ID
   * @param {string} destination - Destination path
   * @returns {Promise<object>} - Download result
   */
  async downloadModrinthMod(modId, versionId, destination) {
    // Get the file URL from Modrinth API
    const modrinthService = require('./modrinth');
    const files = await modrinthService.getVersionFiles(versionId);
    
    if (!files || files.length === 0) {
      throw new Error('No files found for this version');
    }

    const file = files[0]; // Get primary file
    
    return this.addToQueue({
      url: file.url,
      destination,
      name: file.filename,
      size: file.size,
    });
  }

  /**
   * Download a mod from CurseForge
   * @param {number} modId - Mod ID
   * @param {number} fileId - File ID
   * @param {string} destination - Destination path
   * @returns {Promise<object>} - Download result
   */
  async downloadCurseForgeMod(modId, fileId, destination) {
    const CurseForgeService = require('./curseforge');
    // Would need an instance with API key
    const cfService = new CurseForgeService(process.env.CURSEFORGE_API_KEY);
    
    const downloadUrl = await cfService.getDownloadUrl(modId, fileId);
    
    if (!downloadUrl) {
      throw new Error('Could not get download URL');
    }

    return this.addToQueue({
      url: downloadUrl,
      destination,
      name: `mod-${modId}.jar`,
    });
  }

  /**
   * Retry a failed download
   * @param {string} itemId - Download item ID
   */
  retryDownload(itemId) {
    const item = Array.from(this.activeDownloads.values())
      .find(i => i.id === itemId) || 
      this.queue.find(i => i.id === itemId);
    
    if (item && (item.status === 'failed' || item.status === 'cancelled')) {
      item.status = 'queued';
      item.progress = 0;
      item.error = null;
      this.queue.push(item);
      this.processQueue();
    }
  }
}

module.exports = new DownloadManager();
