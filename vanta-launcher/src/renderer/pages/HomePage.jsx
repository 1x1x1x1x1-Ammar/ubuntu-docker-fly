import React from 'react';

const HomePage = () => {
  return (
    <div className="p-8">
      {/* Welcome Section */}
      <div className="mb-8">
        <h2 className="text-3xl font-bold text-white mb-2">Welcome to VANTA</h2>
        <p className="text-vanta-muted">Your ultimate Minecraft modding experience</p>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-4 gap-4 mb-8">
        <div className="card">
          <div className="text-2xl font-bold text-vanta-green">0</div>
          <div className="text-sm text-vanta-muted">Profiles</div>
        </div>
        <div className="card">
          <div className="text-2xl font-bold text-vanta-green">0</div>
          <div className="text-sm text-vanta-muted">Mods Installed</div>
        </div>
        <div className="card">
          <div className="text-2xl font-bold text-vanta-green">0</div>
          <div className="text-sm text-vanta-muted">Modpacks</div>
        </div>
        <div className="card">
          <div className="text-2xl font-bold text-vanta-green">v1.0.0</div>
          <div className="text-sm text-vanta-muted">Launcher Version</div>
        </div>
      </div>

      {/* Featured Content */}
      <div className="grid grid-cols-2 gap-6">
        {/* Quick Start */}
        <div className="card-glass">
          <h3 className="text-xl font-semibold mb-4">Quick Start</h3>
          <div className="space-y-3">
            <div className="flex items-start gap-3 p-3 rounded-lg bg-vanta-card/50">
              <div className="w-8 h-8 rounded-full bg-vanta-green/20 flex items-center justify-center text-vanta-green font-bold">1</div>
              <div>
                <p className="font-medium text-white">Create a Profile</p>
                <p className="text-sm text-vanta-muted">Set up your first Minecraft profile</p>
              </div>
            </div>
            <div className="flex items-start gap-3 p-3 rounded-lg bg-vanta-card/50">
              <div className="w-8 h-8 rounded-full bg-vanta-green/20 flex items-center justify-center text-vanta-green font-bold">2</div>
              <div>
                <p className="font-medium text-white">Install Mods</p>
                <p className="text-sm text-vanta-muted">Browse and install from Modrinth or CurseForge</p>
              </div>
            </div>
            <div className="flex items-start gap-3 p-3 rounded-lg bg-vanta-card/50">
              <div className="w-8 h-8 rounded-full bg-vanta-green/20 flex items-center justify-center text-vanta-green font-bold">3</div>
              <div>
                <p className="font-medium text-white">Launch Game</p>
                <p className="text-sm text-vanta-muted">Start playing with your mods</p>
              </div>
            </div>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="card-glass">
          <h3 className="text-xl font-semibold mb-4">Recent Activity</h3>
          <div className="flex flex-col items-center justify-center h-48 text-vanta-muted">
            <svg className="w-16 h-16 mb-4 opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p>No recent activity</p>
          </div>
        </div>
      </div>

      {/* News Section */}
      <div className="mt-8 card-glass">
        <h3 className="text-xl font-semibold mb-4">Latest News</h3>
        <div className="space-y-4">
          <div className="flex gap-4 p-4 rounded-lg bg-vanta-card/50 hover:bg-vanta-card transition-colors cursor-pointer">
            <div className="w-32 h-20 rounded-lg bg-vanta-border flex-shrink-0"></div>
            <div className="flex-1">
              <h4 className="font-medium text-white mb-1">VANTA Launcher v1.0.0 Released</h4>
              <p className="text-sm text-vanta-muted line-clamp-2">
                The first stable release of VANTA is now available with full Modrinth and CurseForge integration...
              </p>
              <div className="flex items-center gap-4 mt-2 text-xs text-vanta-muted">
                <span>Dec 2024</span>
                <span>•</span>
                <span>Announcement</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;
