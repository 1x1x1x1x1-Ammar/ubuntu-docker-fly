const fs = require('fs-extra');
const path = require('path');

class ConfigManager {
  constructor() {
    this.vantaDir = process.platform === 'win32'
      ? path.join(process.env.APPDATA, 'VANTA')
      : path.join(process.env.HOME, '.vanta');
    
    this.configPath = path.join(this.vantaDir, 'config.json');
    this.accountsPath = path.join(this.vantaDir, 'accounts.json');
    this.profilesDir = path.join(this.vantaDir, 'profiles');
    this.modsDir = path.join(this.vantaDir, 'mods');
    this.skinsDir = path.join(this.vantaDir, 'skins');
    this.hudLayoutsDir = path.join(this.vantaDir, 'hud-layouts');
    this.waypointsDir = path.join(this.vantaDir, 'waypoints');
    this.cacheDir = path.join(this.vantaDir, 'cache');
    
    this.ensureDirectories();
  }

  /**
   * Ensure all required directories exist
   */
  ensureDirectories() {
    fs.ensureDirSync(this.vantaDir);
    fs.ensureDirSync(this.profilesDir);
    fs.ensureDirSync(this.modsDir);
    fs.ensureDirSync(this.skinsDir);
    fs.ensureDirSync(this.hudLayoutsDir);
    fs.ensureDirSync(this.waypointsDir);
    fs.ensureDirSync(this.cacheDir);
    fs.ensureDirSync(path.join(this.cacheDir, 'mod-icons'));
  }

  /**
   * Load global config
   * @returns {Promise<object>} - Config object
   */
  async loadConfig() {
    try {
      if (await fs.pathExists(this.configPath)) {
        return await fs.readJson(this.configPath);
      }
      return this.getDefaultConfig();
    } catch (error) {
      console.error('Error loading config:', error);
      return this.getDefaultConfig();
    }
  }

  /**
   * Save global config
   * @param {object} config - Config to save
   * @returns {Promise<boolean>} - Success status
   */
  async saveConfig(config) {
    try {
      await fs.writeJson(this.configPath, config, { spaces: 2 });
      return true;
    } catch (error) {
      console.error('Error saving config:', error);
      return false;
    }
  }

  /**
   * Get default config
   * @returns {object} - Default config
   */
  getDefaultConfig() {
    return {
      autoUpdate: true,
      updateChannel: 'stable',
      closeToTray: false,
      launchOnStart: false,
      language: 'en',
      theme: 'dark',
      accentColor: '#00FF87',
      particleIntensity: 'medium',
      animationSpeed: 'normal',
      fontSize: 100,
      compactMode: false,
      javaPath: '',
      allocatedRam: 4,
      jvmArgs: '',
      resolution: { width: 1920, height: 1080 },
      keepLauncherOpen: true,
      discordRichPresence: true,
      downloadMirror: 'default',
    };
  }

  /**
   * Load accounts
   * @returns {Promise<Array>} - Accounts array
   */
  async loadAccounts() {
    try {
      if (await fs.pathExists(this.accountsPath)) {
        return await fs.readJson(this.accountsPath);
      }
      return [];
    } catch (error) {
      console.error('Error loading accounts:', error);
      return [];
    }
  }

  /**
   * Save accounts
   * @param {Array} accounts - Accounts to save
   * @returns {Promise<boolean>} - Success status
   */
  async saveAccounts(accounts) {
    try {
      await fs.writeJson(this.accountsPath, accounts, { spaces: 2 });
      return true;
    } catch (error) {
      console.error('Error saving accounts:', error);
      return false;
    }
  }

  /**
   * Load profile
   * @param {string} profileId - Profile ID
   * @returns {Promise<object|null>} - Profile object
   */
  async loadProfile(profileId) {
    try {
      const profilePath = path.join(this.profilesDir, `profile_${profileId}.json`);
      if (await fs.pathExists(profilePath)) {
        return await fs.readJson(profilePath);
      }
      return null;
    } catch (error) {
      console.error('Error loading profile:', error);
      return null;
    }
  }

  /**
   * Save profile
   * @param {object} profile - Profile to save
   * @returns {Promise<boolean>} - Success status
   */
  async saveProfile(profile) {
    try {
      const profilePath = path.join(this.profilesDir, `profile_${profile.id}.json`);
      await fs.writeJson(profilePath, profile, { spaces: 2 });
      return true;
    } catch (error) {
      console.error('Error saving profile:', error);
      return false;
    }
  }

  /**
   * Delete profile
   * @param {string} profileId - Profile ID
   * @returns {Promise<boolean>} - Success status
   */
  async deleteProfile(profileId) {
    try {
      const profilePath = path.join(this.profilesDir, `profile_${profileId}.json`);
      await fs.remove(profilePath);
      return true;
    } catch (error) {
      console.error('Error deleting profile:', error);
      return false;
    }
  }

  /**
   * List all profiles
   * @returns {Promise<Array>} - Array of profiles
   */
  async listProfiles() {
    try {
      const files = await fs.readdir(this.profilesDir);
      const profiles = [];
      
      for (const file of files) {
        if (file.endsWith('.json')) {
          const profile = await fs.readJson(path.join(this.profilesDir, file));
          profiles.push(profile);
        }
      }
      
      return profiles;
    } catch (error) {
      console.error('Error listing profiles:', error);
      return [];
    }
  }

  /**
   * Load installed mods for a profile
   * @param {string} profileId - Profile ID
   * @returns {Promise<Array>} - Array of installed mods
   */
  async loadInstalledMods(profileId) {
    try {
      const modsPath = path.join(this.modsDir, `${profileId}.json`);
      if (await fs.pathExists(modsPath)) {
        return await fs.readJson(modsPath);
      }
      return [];
    } catch (error) {
      console.error('Error loading installed mods:', error);
      return [];
    }
  }

  /**
   * Save installed mods for a profile
   * @param {string} profileId - Profile ID
   * @param {Array} mods - Mods to save
   * @returns {Promise<boolean>} - Success status
   */
  async saveInstalledMods(profileId, mods) {
    try {
      const modsPath = path.join(this.modsDir, `${profileId}.json`);
      await fs.writeJson(modsPath, mods, { spaces: 2 });
      return true;
    } catch (error) {
      console.error('Error saving installed mods:', error);
      return false;
    }
  }

  /**
   * Load skins
   * @returns {Promise<Array>} - Array of skins
   */
  async loadSkins() {
    try {
      const skinsPath = path.join(this.skinsDir, 'skins.json');
      if (await fs.pathExists(skinsPath)) {
        return await fs.readJson(skinsPath);
      }
      return [];
    } catch (error) {
      console.error('Error loading skins:', error);
      return [];
    }
  }

  /**
   * Save skin
   * @param {object} skin - Skin data
   * @returns {Promise<boolean>} - Success status
   */
  async saveSkin(skin) {
    try {
      const skinsPath = path.join(this.skinsDir, 'skins.json');
      let skins = [];
      
      if (await fs.pathExists(skinsPath)) {
        skins = await fs.readJson(skinsPath);
      }
      
      skins.push(skin);
      await fs.writeJson(skinsPath, skins, { spaces: 2 });
      
      // Save skin PNG if provided
      if (skin.pngData) {
        const pngPath = path.join(this.skinsDir, `${skin.id}.png`);
        const base64Data = skin.pngData.replace(/^data:image\/png;base64,/, '');
        await fs.writeFile(pngPath, base64Data, 'base64');
      }
      
      return true;
    } catch (error) {
      console.error('Error saving skin:', error);
      return false;
    }
  }

  /**
   * Load HUD layout
   * @param {string} name - Layout name
   * @returns {Promise<object|null>} - Layout object
   */
  async loadHudLayout(name) {
    try {
      const layoutPath = path.join(this.hudLayoutsDir, `${name}.vanta-hud`);
      if (await fs.pathExists(layoutPath)) {
        return await fs.readJson(layoutPath);
      }
      return null;
    } catch (error) {
      console.error('Error loading HUD layout:', error);
      return null;
    }
  }

  /**
   * Save HUD layout
   * @param {string} name - Layout name
   * @param {object} layout - Layout data
   * @returns {Promise<boolean>} - Success status
   */
  async saveHudLayout(name, layout) {
    try {
      const layoutPath = path.join(this.hudLayoutsDir, `${name}.vanta-hud`);
      await fs.writeJson(layoutPath, layout, { spaces: 2 });
      return true;
    } catch (error) {
      console.error('Error saving HUD layout:', error);
      return false;
    }
  }

  /**
   * Load waypoints for a server
   * @param {string} serverHash - Server hash
   * @returns {Promise<Array>} - Array of waypoints
   */
  async loadWaypoints(serverHash) {
    try {
      const waypointsPath = path.join(this.waypointsDir, `${serverHash}.json`);
      if (await fs.pathExists(waypointsPath)) {
        return await fs.readJson(waypointsPath);
      }
      return [];
    } catch (error) {
      console.error('Error loading waypoints:', error);
      return [];
    }
  }

  /**
   * Save waypoints for a server
   * @param {string} serverHash - Server hash
   * @param {Array} waypoints - Waypoints to save
   * @returns {Promise<boolean>} - Success status
   */
  async saveWaypoints(serverHash, waypoints) {
    try {
      const waypointsPath = path.join(this.waypointsDir, `${serverHash}.json`);
      await fs.writeJson(waypointsPath, waypoints, { spaces: 2 });
      return true;
    } catch (error) {
      console.error('Error saving waypoints:', error);
      return false;
    }
  }

  /**
   * Clear cache
   * @returns {Promise<boolean>} - Success status
   */
  async clearCache() {
    try {
      await fs.emptyDir(this.cacheDir);
      return true;
    } catch (error) {
      console.error('Error clearing cache:', error);
      return false;
    }
  }

  /**
   * Export all settings as backup
   * @param {string} destination - Destination path
   * @returns {Promise<boolean>} - Success status
   */
  async exportBackup(destination) {
    try {
      const AdmZip = require('adm-zip');
      const zip = new AdmZip();
      
      zip.addLocalFolder(this.vantaDir, 'vanta');
      await zip.writeZipPromise(destination);
      
      return true;
    } catch (error) {
      console.error('Error exporting backup:', error);
      return false;
    }
  }

  /**
   * Import settings from backup
   * @param {string} source - Source zip path
   * @returns {Promise<boolean>} - Success status
   */
  async importBackup(source) {
    try {
      const AdmZip = require('adm-zip');
      const zip = new AdmZip(source);
      
      // Extract to vanta dir
      await zip.extractAllTo(this.vantaDir, true);
      
      return true;
    } catch (error) {
      console.error('Error importing backup:', error);
      return false;
    }
  }
}

module.exports = new ConfigManager();
