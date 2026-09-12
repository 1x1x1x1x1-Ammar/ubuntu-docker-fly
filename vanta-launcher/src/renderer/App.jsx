import React, { useState, useEffect } from 'react';
import ParticleBackground from './components/ParticleBackground';
import Sidebar from './components/Sidebar';
import TitleBar from './components/TitleBar';
import HomePage from './pages/HomePage';
import ProfilesPage from './pages/ProfilesPage';
import ModsPage from './pages/ModsPage';
import ModpacksPage from './pages/ModpacksPage';
import SkinsPage from './pages/SkinsPage';
import SettingsPage from './pages/SettingsPage';

function App() {
  const [currentPage, setCurrentPage] = useState('home');
  const [config, setConfig] = useState(null);

  useEffect(() => {
    loadConfig();
  }, []);

  const loadConfig = async () => {
    try {
      const savedConfig = await window.electronAPI.getConfig();
      setConfig(savedConfig || {});
    } catch (error) {
      console.error('Failed to load config:', error);
      setConfig({});
    }
  };

  const saveConfig = async (newConfig) => {
    try {
      await window.electronAPI.saveConfig(newConfig);
      setConfig(newConfig);
    } catch (error) {
      console.error('Failed to save config:', error);
    }
  };

  const renderPage = () => {
    switch (currentPage) {
      case 'home':
        return <HomePage />;
      case 'profiles':
        return <ProfilesPage />;
      case 'mods':
        return <ModsPage />;
      case 'modpacks':
        return <ModpacksPage />;
      case 'skins':
        return <SkinsPage />;
      case 'settings':
        return <SettingsPage config={config} saveConfig={saveConfig} />;
      default:
        return <HomePage />;
    }
  };

  return (
    <div id="app-container" className="bg-vanta-black text-white">
      <canvas id="particles-canvas" />
      <TitleBar />
      <div className="flex h-full pt-8">
        <Sidebar currentPage={currentPage} setCurrentPage={setCurrentPage} />
        <main className="flex-1 overflow-auto relative z-10">
          {renderPage()}
        </main>
      </div>
    </div>
  );
}

export default App;
