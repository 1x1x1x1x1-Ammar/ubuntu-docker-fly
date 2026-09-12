import React, { useState } from 'react';

const SettingsPage = ({ config, saveConfig }) => {
  const [activeTab, setActiveTab] = useState('general');

  const tabs = [
    { id: 'general', label: 'General' },
    { id: 'game', label: 'Game' },
    { id: 'appearance', label: 'Appearance' },
    { id: 'accounts', label: 'Accounts' },
    { id: 'advanced', label: 'Advanced' },
  ];

  return (
    <div className="p-8">
      <h2 className="text-3xl font-bold text-white mb-6">Settings</h2>

      <div className="flex gap-6">
        {/* Tabs */}
        <div className="w-48 flex-shrink-0">
          <nav className="space-y-2">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`w-full text-left px-4 py-3 rounded-lg transition-all ${
                  activeTab === tab.id
                    ? 'bg-vanta-green/10 text-vanta-green border-l-2 border-vanta-green'
                    : 'text-vanta-muted hover:text-white hover:bg-vanta-card'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        {/* Content */}
        <div className="flex-1 card-glass p-6">
          {activeTab === 'general' && <GeneralSettings config={config} saveConfig={saveConfig} />}
          {activeTab === 'game' && <GameSettings config={config} saveConfig={saveConfig} />}
          {activeTab === 'appearance' && <AppearanceSettings config={config} saveConfig={saveConfig} />}
          {activeTab === 'accounts' && <AccountsSettings />}
          {activeTab === 'advanced' && <AdvancedSettings config={config} saveConfig={saveConfig} />}
        </div>
      </div>
    </div>
  );
};

const GeneralSettings = ({ config, saveConfig }) => {
  return (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold text-white mb-4">General Settings</h3>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Auto-update VANTA</p>
          <p className="text-sm text-vanta-muted">Automatically download and install updates</p>
        </div>
        <ToggleSwitch />
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Close to System Tray</p>
          <p className="text-sm text-vanta-muted">Minimize to tray instead of closing</p>
        </div>
        <ToggleSwitch />
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Launch Game on Startup</p>
          <p className="text-sm text-vanta-muted">Start Minecraft when launcher opens</p>
        </div>
        <ToggleSwitch />
      </div>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">Language</label>
        <select className="input-dark w-full max-w-xs">
          <option value="en">English</option>
          <option value="es">Español</option>
          <option value="fr">Français</option>
          <option value="de">Deutsch</option>
          <option value="zh">中文</option>
          <option value="ja">日本語</option>
        </select>
      </div>
    </div>
  );
};

const GameSettings = ({ config, saveConfig }) => {
  return (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold text-white mb-4">Game Settings</h3>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">Java Path</label>
        <div className="flex gap-2">
          <input type="text" className="input-dark flex-1" placeholder="Auto-detected: C:/Program Files/Java/jdk-17" />
          <button className="btn-secondary">Browse</button>
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">
          Allocated RAM: 4 GB
        </label>
        <input type="range" min="1" max="16" defaultValue="4" className="w-full accent-vanta-green" />
        <p className="text-xs text-vanta-muted mt-1">Recommended: 4-8 GB for most modpacks</p>
      </div>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">JVM Arguments Preset</label>
        <select className="input-dark w-full max-w-xs">
          <option value="performance">Performance</option>
          <option value="compatibility">Compatibility</option>
          <option value="graalvm">GraalVM</option>
          <option value="custom">Custom</option>
        </select>
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Discord Rich Presence</p>
          <p className="text-sm text-vanta-muted">Show "Playing VANTA Client" on Discord</p>
        </div>
        <ToggleSwitch />
      </div>
    </div>
  );
};

const AppearanceSettings = ({ config, saveConfig }) => {
  return (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold text-white mb-4">Appearance Settings</h3>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">Theme</label>
        <select className="input-dark w-full max-w-xs">
          <option value="dark">Dark (Default)</option>
          <option value="darker">Darker</option>
          <option value="vanta">VANTA Dark</option>
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">Accent Color</label>
        <div className="flex items-center gap-3">
          <input type="color" defaultValue="#00FF87" className="w-10 h-10 rounded cursor-pointer" />
          <input type="text" defaultValue="#00FF87" className="input-dark w-32 font-mono" />
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">Particle Background Intensity</label>
        <select className="input-dark w-full max-w-xs">
          <option value="off">Off</option>
          <option value="low">Low</option>
          <option value="medium">Medium</option>
          <option value="high">High</option>
        </select>
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Compact Mode</p>
          <p className="text-sm text-vanta-muted">Smaller cards and tighter spacing</p>
        </div>
        <ToggleSwitch />
      </div>
    </div>
  );
};

const AccountsSettings = () => {
  return (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold text-white mb-4">Account Settings</h3>

      <div className="card bg-vanta-card/50 p-4 flex items-center gap-4">
        <div className="w-12 h-12 rounded-full bg-vanta-green flex items-center justify-center">
          <span className="text-vanta-black font-bold text-lg">U</span>
        </div>
        <div className="flex-1">
          <p className="font-medium text-white">User</p>
          <p className="text-sm text-vanta-muted">Offline Account</p>
        </div>
        <button className="btn-secondary">Remove</button>
      </div>

      <button className="btn-primary w-full">
        <svg className="w-5 h-5 inline mr-2" fill="currentColor" viewBox="0 0 24 24">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z"/>
        </svg>
        Add Microsoft Account
      </button>

      <button className="btn-secondary w-full">
        Add Offline Account
      </button>
    </div>
  );
};

const AdvancedSettings = ({ config, saveConfig }) => {
  return (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold text-white mb-4">Advanced Settings</h3>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Developer Tools</p>
          <p className="text-sm text-vanta-muted">Enable DevTools for debugging</p>
        </div>
        <ToggleSwitch />
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Clear Launcher Cache</p>
          <p className="text-sm text-vanta-muted">Free up disk space</p>
        </div>
        <button className="btn-secondary">Clear Cache</button>
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Export Settings</p>
          <p className="text-sm text-vanta-muted">Backup all VANTA settings</p>
        </div>
        <button className="btn-secondary">Export .vanta-config</button>
      </div>

      <div className="flex items-center justify-between">
        <div>
          <p className="font-medium text-white">Import Settings</p>
          <p className="text-sm text-vanta-muted">Restore from backup file</p>
        </div>
        <button className="btn-secondary">Import .vanta-config</button>
      </div>

      <div>
        <label className="block text-sm font-medium text-vanta-muted mb-2">Config Directory</label>
        <div className="flex items-center gap-2 p-3 bg-vanta-card rounded-lg">
          <code className="text-vanta-green font-mono text-sm flex-1">
            %APPDATA%/VANTA/
          </code>
          <button className="btn-secondary text-sm">Open</button>
        </div>
      </div>
    </div>
  );
};

const ToggleSwitch = () => {
  const [enabled, setEnabled] = useState(false);
  
  return (
    <button
      onClick={() => setEnabled(!enabled)}
      className={`toggle-switch ${enabled ? 'active' : ''}`}
    />
  );
};

export default SettingsPage;
