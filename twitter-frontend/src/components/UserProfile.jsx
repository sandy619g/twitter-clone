import { useParams } from 'react-router-dom';
import { useEffect, useState } from 'react';
import axios from 'axios';

function UserProfilePage() {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [posts, setPosts] = useState([]);
  const [newPost, setNewPost] = useState('');

  useEffect(() => {
    axios.get(`/api/users/${id}`).then(res => setUser(res.data));
    axios.get(`/api/posts/user/${id}`).then(res => setPosts(res.data));
  }, [id]);

  const handlePost = () => {
    if (!newPost.trim()) return;
    axios.post(`/api/posts/user/${id}`, { content: newPost }).then(() => {
      setNewPost('');
      axios.get(`/api/posts/user/${id}`).then(res => setPosts(res.data));
    });
  };

  if (!user) return <p>Loading...</p>;

  return (
    <div className="container">
      <h2>{user.username}'s Profile</h2>
      {user.avatarUrl && <img src={user.avatarUrl} alt="avatar" width={80} style={{ borderRadius: '50%' }} />}

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
