import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function CreateUserPage() {
  const [username, setUsername] = useState('');
  const [handle, setHandle] = useState('');
  const [location, setLocation] = useState('');
  const [bio, setBio] = useState('');
  const [avatar, setAvatar] = useState(null);
  const navigate = useNavigate();

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatar(file);
    }
  };

  const handleCreate = () => {
    const formData = new FormData();
    formData.append('username', username);
    formData.append('handle', handle);
    formData.append('location', location);
    formData.append('bio', bio);
    if (avatar) formData.append('avatar', avatar);

    axios.post('/api/users', formData)
      .then((response) => {
        alert('User created successfully!');
        // Redirect to the newly created user's profile page
        navigate(`/profile/${response.data.id}`);
      })
      .catch((error) => {
        console.error('Error creating user:', error);
        alert('Error creating user');
      });
  };

  return (
    <div className="container">
      <h2>Create New User</h2>
      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        type="text"
        placeholder="Handle"
        value={handle}
        onChange={(e) => setHandle(e.target.value)}
      />
      <input
        type="text"
        placeholder="Location"
        value={location}
        onChange={(e) => setLocation(e.target.value)}
      />
      <textarea
        placeholder="Bio"
        value={bio}
        onChange={(e) => setBio(e.target.value)}
      />
      <input
        type="file"
        onChange={handleAvatarChange}
        accept="image/*"
      />
      {avatar && (
          <img src={URL.createObjectURL(avatar)} alt="Preview" width={80} style={{ borderRadius: '50%', marginTop: 10 }} />
       )}
      <button onClick={handleCreate}>Create User</button>
    </div>
  );
}

export default CreateUserPage;
