import React from 'react';

const ModsPage = () => {
  return (
    <div className="p-8">
      <h2 className="text-3xl font-bold text-white mb-2">Mods</h2>
      <p className="text-vanta-muted mb-6">Browse and install mods from Modrinth and CurseForge</p>
      
      <div className="card-glass flex items-center justify-center py-16">
        <div className="text-center">
          <svg className="w-20 h-20 text-vanta-muted mb-4 opacity-50 mx-auto" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
          </svg>
          <h3 className="text-xl font-semibold text-white mb-2">Mods Browser</h3>
          <p className="text-vanta-muted">Search and install mods from Modrinth and CurseForge APIs</p>
        </div>
      </div>
    </div>
  );
};

export default ModsPage;
