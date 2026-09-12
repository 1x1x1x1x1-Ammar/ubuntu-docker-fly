import React, { useState } from 'react';

const ProfilesPage = () => {
  const [profiles, setProfiles] = useState([]);
  const [showCreateModal, setShowCreateModal] = useState(false);

  return (
    <div className="p-8">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h2 className="text-3xl font-bold text-white mb-2">Profiles</h2>
          <p className="text-vanta-muted">Manage your Minecraft profiles and installations</p>
        </div>
        <button 
          onClick={() => setShowCreateModal(true)}
          className="btn-primary flex items-center gap-2"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          New Profile
        </button>
      </div>

      {profiles.length === 0 ? (
        <div className="card-glass flex flex-col items-center justify-center py-16">
          <svg className="w-20 h-20 text-vanta-muted mb-4 opacity-50" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
          </svg>
          <h3 className="text-xl font-semibold text-white mb-2">No Profiles Yet</h3>
          <p className="text-vanta-muted mb-4">Create your first profile to get started</p>
          <button 
            onClick={() => setShowCreateModal(true)}
            className="btn-primary"
          >
            Create Profile
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-2 gap-4">
          {profiles.map((profile) => (
            <div key={profile.id} className="card group relative">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-lg bg-vanta-green/20 flex items-center justify-center">
                    <span className="text-vanta-green font-bold text-lg">{profile.name[0]}</span>
                  </div>
                  <div>
                    <h3 className="font-semibold text-white">{profile.name}</h3>
                    <p className="text-sm text-vanta-muted">v{profile.version}</p>
                  </div>
                </div>
                <div className="opacity-0 group-hover:opacity-100 transition-opacity flex gap-2">
                  <button className="p-2 hover:bg-vanta-card rounded-lg transition-colors">
                    <svg className="w-4 h-4 text-vanta-muted hover:text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                    </svg>
                  </button>
                  <button className="p-2 hover:bg-red-500/20 rounded-lg transition-colors">
                    <svg className="w-4 h-4 text-vanta-muted hover:text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
              
              <div className="flex items-center gap-4 text-sm text-vanta-muted mb-4">
                <span>{profile.modCount || 0} mods</span>
                <span>•</span>
                <span>{profile.loader || 'Fabric'}</span>
              </div>

              <div className="flex gap-2">
                <button className="btn-primary flex-1">Launch</button>
                <button className="btn-secondary">Mods</button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Create Profile Modal */}
      {showCreateModal && (
        <CreateProfileModal 
          onClose={() => setShowCreateModal(false)}
          onCreate={(profile) => {
            setProfiles([...profiles, profile]);
            setShowCreateModal(false);
          }}
        />
      )}
    </div>
  );
};

const CreateProfileModal = ({ onClose, onCreate }) => {
  const [name, setName] = useState('');
  const [version, setVersion] = useState('1.20.4');
  const [loader, setLoader] = useState('fabric');

  const handleCreate = () => {
    if (!name.trim()) return;
    
    onCreate({
      id: Date.now().toString(),
      name,
      version,
      loader,
      modCount: 0,
      createdAt: new Date().toISOString(),
    });
  };

  return (
    <div className="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="card-glass w-full max-w-md p-6">
        <h3 className="text-xl font-bold text-white mb-6">Create New Profile</h3>
        
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-vanta-muted mb-2">Profile Name</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className="input-dark w-full"
              placeholder="My Modded Profile"
              autoFocus
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-vanta-muted mb-2">Minecraft Version</label>
            <select
              value={version}
              onChange={(e) => setVersion(e.target.value)}
              className="input-dark w-full"
            >
              <option value="1.20.4">1.20.4</option>
              <option value="1.20.2">1.20.2</option>
              <option value="1.20.1">1.20.1</option>
              <option value="1.19.4">1.19.4</option>
              <option value="1.19.2">1.19.2</option>
              <option value="1.18.2">1.18.2</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-vanta-muted mb-2">Mod Loader</label>
            <div className="flex gap-2">
              <button
                onClick={() => setLoader('fabric')}
                className={`flex-1 py-3 rounded-lg border transition-all ${
                  loader === 'fabric'
                    ? 'bg-vanta-green/20 border-vanta-green text-vanta-green'
                    : 'bg-vanta-card border-vanta-border text-white hover:border-vanta-green'
                }`}
              >
                Fabric
              </button>
              <button
                onClick={() => setLoader('quilt')}
                className={`flex-1 py-3 rounded-lg border transition-all ${
                  loader === 'quilt'
                    ? 'bg-vanta-green/20 border-vanta-green text-vanta-green'
                    : 'bg-vanta-card border-vanta-border text-white hover:border-vanta-green'
                }`}
              >
                Quilt
              </button>
            </div>
          </div>
        </div>

        <div className="flex gap-3 mt-6">
          <button onClick={onClose} className="btn-secondary flex-1">
            Cancel
          </button>
          <button 
            onClick={handleCreate}
            disabled={!name.trim()}
            className="btn-primary flex-1 disabled:opacity-50"
          >
            Create Profile
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilesPage;
