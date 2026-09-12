const axios = require('axios');

const CURSEFORGE_API_BASE = 'https://api.curseforge.com/v1';

class CurseForgeService {
  constructor(apiKey) {
    this.apiKey = apiKey;
    this.headers = {
      'X-API-Key': apiKey,
      'Content-Type': 'application/json'
    };
  }

  /**
   * Set API key
   * @param {string} apiKey - CurseForge API key
   */
  setApiKey(apiKey) {
    this.apiKey = apiKey;
    this.headers['X-API-Key'] = apiKey;
  }

  /**
   * Search for mods on CurseForge
   * @param {string} query - Search query
   * @param {object} options - Search options
   * @returns {Promise<Array>} - Array of mod results
   */
  async searchMods(query, options = {}) {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return [];
    }

    try {
      const params = new URLSearchParams({
        gameId: '432', // Minecraft
        searchFilter: query || '',
        pageSize: options.limit || 20,
        index: options.offset || 0,
      });

      if (options.gameVersion) {
        params.append('gameVersions', options.gameVersion);
      }

      if (options.modLoaderType) {
        params.append('modLoaderType', options.modLoaderType);
      }

      if (options.classId) {
        params.append('classId', options.classId);
      }

      const response = await axios.get(`${CURSEFORGE_API_BASE}/mods/search?${params}`, {
        headers: this.headers
      });

      return response.data.data;
    } catch (error) {
      console.error('CurseForge search error:', error);
      return [];
    }
  }

  /**
   * Get mod details by ID
   * @param {number} modId - Mod ID
   * @returns {Promise<object>} - Mod details
   */
  async getMod(modId) {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return null;
    }

    try {
      const response = await axios.get(`${CURSEFORGE_API_BASE}/mods/${modId}`, {
        headers: this.headers
      });
      return response.data.data;
    } catch (error) {
      console.error('CurseForge get mod error:', error);
      return null;
    }
  }

  /**
   * Get mod files
   * @param {number} modId - Mod ID
   * @returns {Promise<Array>} - Array of files
   */
  async getModFiles(modId) {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return [];
    }

    try {
      const response = await axios.get(`${CURSEFORGE_API_BASE}/mods/${modId}/files`, {
        headers: this.headers
      });
      return response.data.data;
    } catch (error) {
      console.error('CurseForge get mod files error:', error);
      return [];
    }
  }

  /**
   * Get file download URL
   * @param {number} modId - Mod ID
   * @param {number} fileId - File ID
   * @returns {Promise<string>} - Download URL
   */
  async getDownloadUrl(modId, fileId) {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return null;
    }

    try {
      const response = await axios.get(
        `${CURSEFORGE_API_BASE}/mods/${modId}/files/${fileId}/download-url`,
        { headers: this.headers }
      );
      return response.data.data.downloadUrl;
    } catch (error) {
      console.error('CurseForge get download URL error:', error);
      return null;
    }
  }

  /**
   * Get multiple mods by IDs
   * @param {Array<number>} modIds - Array of mod IDs
   * @returns {Promise<Object>} - Object with mod data
   */
  async getMultipleMods(modIds) {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return { data: [] };
    }

    try {
      const response = await axios.post(
        `${CURSEFORGE_API_BASE}/mods`,
        { modIds },
        { headers: this.headers }
      );
      return response.data.data;
    } catch (error) {
      console.error('CurseForge get multiple mods error:', error);
      return { data: [] };
    }
  }

  /**
   * Get featured mods
   * @returns {Promise<Array>} - Array of featured mods
   */
  async getFeaturedMods() {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return [];
    }

    try {
      const response = await axios.post(
        `${CURSEFORGE_API_BASE}/mods/featured`,
        {},
        { headers: this.headers }
      );
      return response.data.data;
    } catch (error) {
      console.error('CurseForge featured mods error:', error);
      return [];
    }
  }

  /**
   * Get mod categories
   * @returns {Promise<Array>} - Array of categories
   */
  async getCategories() {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return [];
    }

    try {
      const response = await axios.get(`${CURSEFORGE_API_BASE}/categories`, {
        headers: this.headers,
        params: { gameId: '432' }
      });
      return response.data.data;
    } catch (error) {
      console.error('CurseForge get categories error:', error);
      return [];
    }
  }

  /**
   * Get game versions
   * @returns {Promise<Array>} - Array of game versions
   */
  async getGameVersions() {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return [];
    }

    try {
      const response = await axios.get(`${CURSEFORGE_API_BASE}/games/432/classifications`, {
        headers: this.headers
      });
      
      // Extract Minecraft versions from the response
      const classifications = response.data.data;
      const minecraftClass = classifications.find(c => c.slug === 'minecraft');
      if (minecraftClass && minecraftClass.latestModVersions) {
        return minecraftClass.latestModVersions;
      }
      return [];
    } catch (error) {
      console.error('CurseForge get game versions error:', error);
      return [];
    }
  }

  /**
   * Search for modpacks
   * @param {string} query - Search query
   * @param {object} options - Search options
   * @returns {Promise<Array>} - Array of modpack results
   */
  async searchModpacks(query, options = {}) {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return [];
    }

    try {
      const params = new URLSearchParams({
        gameId: '432',
        searchFilter: query || '',
        pageSize: options.limit || 20,
        classId: '6', // Modpacks class ID
      });

      if (options.gameVersion) {
        params.append('gameVersions', options.gameVersion);
      }

      const response = await axios.get(`${CURSEFORGE_API_BASE}/mods/search?${params}`, {
        headers: this.headers
      });

      return response.data.data;
    } catch (error) {
      console.error('CurseForge search modpacks error:', error);
      return [];
    }
  }

  /**
   * Get modpack manifest
   * @param {number} modpackId - Modpack ID
   * @param {number} fileId - File ID
   * @returns {Promise<object>} - Modpack manifest
   */
  async getModpackManifest(modpackId, fileId) {
    if (!this.apiKey) {
      console.error('CurseForge API key not set');
      return null;
    }

    try {
      const downloadUrl = await this.getDownloadUrl(modpackId, fileId);
      if (!downloadUrl) return null;

      // Download and parse the manifest from the modpack zip
      // This would require additional handling for zip files
      return null;
    } catch (error) {
      console.error('CurseForge get modpack manifest error:', error);
      return null;
    }
  }

  /**
   * Get addon (mod) by slug
   * @param {string} slug - Mod slug
   * @returns {Promise<object>} - Mod details
   */
  async getModBySlug(slug) {
    // CurseForge doesn't have a direct slug endpoint, need to search
    const results = await this.searchMods(slug, { limit: 1 });
    return results.find(mod => mod.slug === slug) || null;
  }
}

module.exports = CurseForgeService;
