const { app, BrowserWindow, ipcMain, dialog, shell } = require('electron');
const path = require('path');
const fs = require('fs-extra');

let mainWindow;

// Config paths
const VANTA_DIR = process.platform === 'win32'
  ? path.join(process.env.APPDATA, 'VANTA')
  : path.join(process.env.HOME, '.vanta');

const CONFIG_PATH = path.join(VANTA_DIR, 'config.json');
const PROFILES_DIR = path.join(VANTA_DIR, 'profiles');
const MODS_DIR = path.join(VANTA_DIR, 'mods');
const SKINS_DIR = path.join(VANTA_DIR, 'skins');

// Ensure directories exist
function ensureDirectories() {
  fs.ensureDirSync(VANTA_DIR);
  fs.ensureDirSync(PROFILES_DIR);
  fs.ensureDirSync(MODS_DIR);
  fs.ensureDirSync(SKINS_DIR);
  fs.ensureDirSync(path.join(VANTA_DIR, 'cache', 'mod-icons'));
  fs.ensureDirSync(path.join(VANTA_DIR, 'hud-layouts'));
  fs.ensureDirSync(path.join(VANTA_DIR, 'waypoints'));
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    minWidth: 1200,
    minHeight: 700,
    frame: false,
    backgroundColor: '#0a0a0a',
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      preload: path.join(__dirname, 'preload.js')
    },
    icon: path.join(__dirname, '../public/icon.png'),
    show: false,
    transparent: true
  });

  // Load the app
  const startUrl = process.env.VITE_DEV_SERVER_URL || 
    `file://${path.join(__dirname, '../dist/index.html')}`;
  
  mainWindow.loadURL(startUrl);

  // Show window when ready
  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
  });

  // Open DevTools in development
  if (process.env.NODE_ENV === 'development') {
    mainWindow.webContents.openDevTools();
  }

  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// IPC Handlers
ipcMain.handle('get-vanta-dir', () => {
  return VANTA_DIR;
});

ipcMain.handle('get-config', async () => {
  try {
    if (await fs.pathExists(CONFIG_PATH)) {
      return await fs.readJson(CONFIG_PATH);
    }
    return {};
  } catch (error) {
    console.error('Error reading config:', error);
    return {};
  }
});

ipcMain.handle('save-config', async (event, config) => {
  try {
    await fs.writeJson(CONFIG_PATH, config, { spaces: 2 });
    return true;
  } catch (error) {
    console.error('Error saving config:', error);
    return false;
  }
});

ipcMain.handle('show-folder-dialog', async () => {
  const result = await dialog.showOpenDialog(mainWindow, {
    properties: ['openDirectory'],
    title: 'Select Folder'
  });
  return result.canceled ? null : result.filePaths[0];
});

ipcMain.handle('open-path-in-explorer', async (event, filePath) => {
  shell.showItemInFolder(filePath);
});

ipcMain.handle('get-profiles', async () => {
  try {
    const files = await fs.readdir(PROFILES_DIR);
    const profiles = [];
    for (const file of files) {
      if (file.endsWith('.json')) {
        const profile = await fs.readJson(path.join(PROFILES_DIR, file));
        profiles.push(profile);
      }
    }
    return profiles;
  } catch (error) {
    console.error('Error reading profiles:', error);
    return [];
  }
});

ipcMain.handle('save-profile', async (event, profile) => {
  try {
    const filePath = path.join(PROFILES_DIR, `profile_${profile.id}.json`);
    await fs.writeJson(filePath, profile, { spaces: 2 });
    return true;
  } catch (error) {
    console.error('Error saving profile:', error);
    return false;
  }
});

ipcMain.handle('delete-profile', async (event, profileId) => {
  try {
    const filePath = path.join(PROFILES_DIR, `profile_${profileId}.json`);
    await fs.remove(filePath);
    return true;
  } catch (error) {
    console.error('Error deleting profile:', error);
    return false;
  }
});

ipcMain.handle('get-installed-mods', async (event, profileId) => {
  try {
    const modsPath = path.join(MODS_DIR, `${profileId}.json`);
    if (await fs.pathExists(modsPath)) {
      return await fs.readJson(modsPath);
    }
    return [];
  } catch (error) {
    console.error('Error reading installed mods:', error);
    return [];
  }
});

ipcMain.handle('save-installed-mods', async (event, profileId, mods) => {
  try {
    const modsPath = path.join(MODS_DIR, `${profileId}.json`);
    await fs.writeJson(modsPath, mods, { spaces: 2 });
    return true;
  } catch (error) {
    console.error('Error saving installed mods:', error);
    return false;
  }
});

ipcMain.handle('get-skins', async () => {
  try {
    const skinsPath = path.join(SKINS_DIR, 'skins.json');
    if (await fs.pathExists(skinsPath)) {
      return await fs.readJson(skinsPath);
    }
    return [];
  } catch (error) {
    console.error('Error reading skins:', error);
    return [];
  }
});

ipcMain.handle('save-skin', async (event, skinData) => {
  try {
    const skinsPath = path.join(SKINS_DIR, 'skins.json');
    let skins = [];
    if (await fs.pathExists(skinsPath)) {
      skins = await fs.readJson(skinsPath);
    }
    skins.push(skinData);
    await fs.writeJson(skinsPath, skins, { spaces: 2 });
    
    // Save skin PNG
    if (skinData.pngData) {
      const pngPath = path.join(SKINS_DIR, `${skinData.id}.png`);
      const base64Data = skinData.pngData.replace(/^data:image\/png;base64,/, '');
      await fs.writeFile(pngPath, base64Data, 'base64');
    }
    return true;
  } catch (error) {
    console.error('Error saving skin:', error);
    return false;
  }
});

ipcMain.handle('minimize-window', () => {
  if (mainWindow) mainWindow.minimize();
});

ipcMain.handle('maximize-window', () => {
  if (mainWindow) {
    if (mainWindow.isMaximized()) {
      mainWindow.unmaximize();
    } else {
      mainWindow.maximize();
    }
  }
});

ipcMain.handle('close-window', () => {
  if (mainWindow) mainWindow.close();
});

// App lifecycle
app.whenReady().then(() => {
  ensureDirectories();
  createWindow();
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});
