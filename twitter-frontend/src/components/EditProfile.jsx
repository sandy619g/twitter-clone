import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';

function EditProfile() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: '',
    handle: '',
    location: '',
    bio: '',
    avatar: null,
  });

  useEffect(() => {
    api.get(`/api/users/${id}`).then(res => {
      const { username, handle, location, bio } = res.data;
      setForm({ username, handle, location, bio, avatar: null });
    });
  }, [id]);

  const handleChange = e => {
    const { name, value, files } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: files ? files[0] : value,
    }));
  };

  const handleSubmit = async e => {
    e.preventDefault();
    const data = new FormData();
    Object.entries(form).forEach(([key, value]) => {
      if (value) data.append(key, value);
    });

    try {
      await api.put(`/api/users/${id}`, data, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      navigate(`/user/${id}`);
    } catch (err) {
      alert("Update failed.");
    }
  };

  return (
    <div className="container">
      <h2>Edit Profile</h2>
      <form onSubmit={handleSubmit}>
        <input name="username" value={form.username} onChange={handleChange} placeholder="Username" />
        <input name="handle" value={form.handle} onChange={handleChange} placeholder="Handle" />
        <input name="location" value={form.location} onChange={handleChange} placeholder="Location" />
        <textarea name="bio" value={form.bio} onChange={handleChange} placeholder="Bio" />
        <input type="file" name="avatar" onChange={handleChange} />
        <button type="submit">Update</button>
      </form>
    </div>
  );
}

export default EditProfile;
