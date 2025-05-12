import { useParams, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../api/axios';

function UserProfilePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [posts, setPosts] = useState([]);
  const [newPost, setNewPost] = useState('');

  useEffect(() => {
    api.get(`/api/users/${id}`).then(res => setUser(res.data));
    api.get(`/api/posts/user/${id}`).then(res => setPosts(res.data));
  }, [id]);

  const handlePost = () => {
    if (!newPost.trim()) return;
    api.post(`/api/posts/user/${id}`, { content: newPost }).then(() => {
      setNewPost('');
      api.get(`/api/posts/user/${id}`).then(res => setPosts(res.data));
    });
  };

  const handleDelete = () => {
      if (confirm("Are you sure you want to delete your profile?")) {
        api.delete(`/api/users/${id}`).then(() => {
          navigate('/');
        });
      }
    };

  if (!user) return <p>Loading...</p>;

const backendUrl = import.meta.env.VITE_API_URL;
const avatarUrl = user.avatarUrl?.startsWith('http')
  ? user.avatarUrl
  : `${backendUrl}${user.avatarUrl}`;

  return (
  <div className="container">
    <div  style={{display: 'flex', flexDirection:"row", gap: "4rem"}}>
    <div>
      <h2>{user.username}'s Profile</h2>
      {user.avatarUrl && <img src={avatarUrl} alt="avatar" width={180} style={{ borderRadius: '50%' }} />}
      </div>
      <div style={{ marginTop: 30 }}>
        <h3>User Information</h3>
        <p><strong>Username:</strong> {user.username}</p>
        <p><strong>Handle:</strong> {user.handle}</p>
        <p><strong>Location:</strong> {user.location}</p>
        <p><strong>Bio:</strong> {user.bio}</p>

        <div style={{ marginTop: 20 }}>
            <button onClick={() => navigate(`/edit-profile/${id}`)}>Edit Profile</button>
            <button onClick={handleDelete} style={{ marginLeft: 10 }}>Delete Profile</button>
        </div>
      </div>
      </div>
      <div style={{ marginTop: 30 }}>
        <h3>Create New Post</h3>
        <textarea
          maxLength={280}
          placeholder="What's on your mind?"
          value={newPost}
          onChange={e => setNewPost(e.target.value)}
        />
        <button onClick={handlePost}>Post</button>
      </div>

      <div style={{ marginTop: 40 }}>
        <h3>Posts</h3>
        {posts.length === 0 && <p>No posts yet.</p>}
        {posts.map(post => (
          <div className="post-card" key={post.id}>
            <p>{post.content}</p>
            <small>{new Date(post.createdAt).toLocaleString()}</small>
          </div>
        ))}
      </div>
    </div>
  );
}


export default UserProfilePage;
