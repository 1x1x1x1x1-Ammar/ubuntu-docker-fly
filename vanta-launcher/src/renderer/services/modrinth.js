const axios = require('axios');

const MODRINTH_API_BASE = 'https://api.modrinth.com/v2';

class ModrinthService {
  /**
   * Search for mods on Modrinth
   * @param {string} query - Search query
   * @param {object} options - Search options
   * @returns {Promise<Array>} - Array of mod results
   */
  async searchMods(query, options = {}) {
    try {
      const params = new URLSearchParams({
        query: query || '',
        limit: options.limit || 20,
        offset: options.offset || 0,
      });

      if (options.gameVersion) {
        params.append('game_versions', `["${options.gameVersion}"]`);
      }
      
      if (options.loader) {
        params.append('loaders', `["${options.loader}"]`);
      }

      if (options.category) {
        params.append('categories', `["${options.category}"]`);
      }

      const response = await axios.get(`${MODRINTH_API_BASE}/search?${params}`);
      return response.data.hits;
    } catch (error) {
      console.error('Modrinth search error:', error);
      return [];
    }
  }

  /**
   * Get mod details by ID or slug
   * @param {string} modId - Mod ID or slug
   * @returns {Promise<object>} - Mod details
   */
  async getMod(modId) {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/project/${modId}`);
      return response.data;
    } catch (error) {
      console.error('Modrinth get mod error:', error);
      return null;
    }
  }

  /**
   * Get mod versions
   * @param {string} modId - Mod ID
   * @returns {Promise<Array>} - Array of versions
   */
  async getVersions(modId) {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/project/${modId}/version`);
      return response.data;
    } catch (error) {
      console.error('Modrinth get versions error:', error);
      return [];
    }
  }

  /**
   * Get version files
   * @param {string} versionId - Version ID
   * @returns {Promise<Array>} - Array of files
   */
  async getVersionFiles(versionId) {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/version/${versionId}/files`);
      return response.data;
    } catch (error) {
      console.error('Modrinth get version files error:', error);
      return [];
    }
  }

  /**
   * Get multiple mods by IDs
   * @param {Array<string>} modIds - Array of mod IDs
   * @returns {Promise<Object>} - Object with mod IDs as keys
   */
  async getMultipleMods(modIds) {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/projects`, {
        params: { ids: JSON.stringify(modIds) }
      });
      
      // Convert array to object with IDs as keys
      const modsObj = {};
      response.data.forEach(mod => {
        modsObj[mod.id] = mod;
      });
      return modsObj;
    } catch (error) {
      console.error('Modrinth get multiple mods error:', error);
      return {};
    }
  }

  /**
   * Get featured mods
   * @returns {Promise<Array>} - Array of featured mods
   */
  async getFeaturedMods() {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/search`, {
        params: {
          index: 'downloads',
          limit: 10
        }
      });
      return response.data.hits;
    } catch (error) {
      console.error('Modrinth featured mods error:', error);
      return [];
    }
  }

  /**
   * Get mod categories
   * @returns {Promise<Array>} - Array of categories
   */
  async getCategories() {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/tag/category`);
      return response.data;
    } catch (error) {
      console.error('Modrinth get categories error:', error);
      return [];
    }
  }

  /**
   * Get game versions
   * @returns {Promise<Array>} - Array of game versions
   */
  async getGameVersions() {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/tag/game_version`);
      return response.data;
    } catch (error) {
      console.error('Modrinth get game versions error:', error);
      return [];
    }
  }

  /**
   * Get loaders
   * @returns {Promise<Array>} - Array of loaders
   */
  async getLoaders() {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/tag/loader`);
      return response.data;
    } catch (error) {
      console.error('Modrinth get loaders error:', error);
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
    try {
      const params = new URLSearchParams({
        query: query || '',
        limit: options.limit || 20,
        index: 'downloads',
      });

      if (options.gameVersion) {
        params.append('game_versions', `["${options.gameVersion}"]`);
      }

      const response = await axios.get(`${MODRINTH_API_BASE}/search?${params}`, {
        headers: {
          'Content-Type': 'application/json'
        }
      });

      return response.data.hits.filter(hit => hit.categories && hit.categories.includes('modpack'));
    } catch (error) {
      console.error('Modrinth search modpacks error:', error);
      return [];
    }
  }

  /**
   * Get modpack files
   * @param {string} modpackId - Modpack ID
   * @param {string} versionId - Version ID
   * @returns {Promise<Array>} - Array of files
   */
  async getModpackFiles(modpackId, versionId) {
    try {
      const response = await axios.get(`${MODRINTH_API_BASE}/version/${versionId}/files`);
      return response.data;
    } catch (error) {
      console.error('Modrinth get modpack files error:', error);
      return [];
    }
  }
}

module.exports = new ModrinthService();
