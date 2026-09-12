const { ipcRenderer } = require('electron');

window.electronAPI = {
  getVantaDir: () => ipcRenderer.invoke('get-vanta-dir'),
  getConfig: () => ipcRenderer.invoke('get-config'),
  saveConfig: (config) => ipcRenderer.invoke('save-config', config),
  showFolderDialog: () => ipcRenderer.invoke('show-folder-dialog'),
  openPathInExplorer: (filePath) => ipcRenderer.invoke('open-path-in-explorer', filePath),
  getProfiles: () => ipcRenderer.invoke('get-profiles'),
  saveProfile: (profile) => ipcRenderer.invoke('save-profile', profile),
  deleteProfile: (profileId) => ipcRenderer.invoke('delete-profile', profileId),
  getInstalledMods: (profileId) => ipcRenderer.invoke('get-installed-mods', profileId),
  saveInstalledMods: (profileId, mods) => ipcRenderer.invoke('save-installed-mods', profileId, mods),
  getSkins: () => ipcRenderer.invoke('get-skins'),
  saveSkin: (skinData) => ipcRenderer.invoke('save-skin', skinData),
  minimizeWindow: () => ipcRenderer.invoke('minimize-window'),
  maximizeWindow: () => ipcRenderer.invoke('maximize-window'),
  closeWindow: () => ipcRenderer.invoke('close-window')
};
