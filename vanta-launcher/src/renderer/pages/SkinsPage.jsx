import React from 'react';

const SkinsPage = () => {
  return (
    <div className="p-8">
      <h2 className="text-3xl font-bold text-white mb-2">Skins</h2>
      <p className="text-vanta-muted mb-6">Manage your Minecraft skins with 3D preview</p>
      
      <div className="card-glass flex items-center justify-center py-16">
        <div className="text-center">
          <svg className="w-20 h-20 text-vanta-muted mb-4 opacity-50 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          <h3 className="text-xl font-semibold text-white mb-2">Skin System</h3>
          <p className="text-vanta-muted">Upload, preview, and manage skins with 3D model viewer</p>
        </div>
      </div>
    </div>
  );
};

export default SkinsPage;
