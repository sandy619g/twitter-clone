import { useState, useEffect } from 'react';
import axios from 'axios';

function CreateUserPage() {
  const [username, setUsername] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');

  const handleCreate = () => {
    axios.post('/api/users', { username, avatarUrl }).then(() => {
      setUsername('');
      setAvatarUrl('');
      alert('User created!');
    });
  };

  return (
    <div className="container">
      <h2>Create New User</h2>
      <input placeholder="Username" value={username} onChange={e => setUsername(e.target.value)} />
      <input placeholder="Avatar URL (optional)" value={avatarUrl} onChange={e => setAvatarUrl(e.target.value)} />
      <button onClick={handleCreate}>Create User</button>
    </div>
  );
}

export default CreateUserPage;
