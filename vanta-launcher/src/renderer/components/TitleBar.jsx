import React from 'react';

const TitleBar = () => {
  const handleMinimize = async () => {
    await window.electronAPI.minimizeWindow();
  };

  const handleMaximize = async () => {
    await window.electronAPI.maximizeWindow();
  };

  const handleClose = async () => {
    await window.electronAPI.closeWindow();
  };

  return (
    <div className="fixed top-0 left-0 right-0 h-8 bg-vanta-dark flex items-center justify-between px-4 z-50"
         style={{ WebkitAppRegion: 'drag' }}>
      <div className="flex items-center gap-2">
        <div className="w-3 h-3 rounded-full bg-vanta-green"></div>
        <span className="text-sm font-semibold text-white ml-2">VANTA Launcher</span>
      </div>
      
      <div className="flex items-center gap-4" style={{ WebkitAppRegion: 'no-drag' }}>
        <button
          onClick={handleMinimize}
          className="w-3 h-3 rounded-full bg-yellow-500 hover:bg-yellow-400 transition-colors"
        />
        <button
          onClick={handleMaximize}
          className="w-3 h-3 rounded-full bg-blue-500 hover:bg-blue-400 transition-colors"
        />
        <button
          onClick={handleClose}
          className="w-3 h-3 rounded-full bg-red-500 hover:bg-red-400 transition-colors"
        />
      </div>
    </div>
  );
};

export default TitleBar;
