import React from 'react';

const ModpacksPage = () => {
  return (
    <div className="p-8">
      <h2 className="text-3xl font-bold text-white mb-2">Modpacks</h2>
      <p className="text-vanta-muted mb-6">Browse and install modpacks from Modrinth and CurseForge</p>
      
      <div className="card-glass flex items-center justify-center py-16">
        <div className="text-center">
          <svg className="w-20 h-20 text-vanta-muted mb-4 opacity-50 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
          </svg>
          <h3 className="text-xl font-semibold text-white mb-2">Modpacks Browser</h3>
          <p className="text-vanta-muted">Discover and install complete modpacks with one click</p>
        </div>
      </div>
    </div>
  );
};

export default ModpacksPage;
