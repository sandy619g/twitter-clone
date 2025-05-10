import { useEffect, useState } from 'react';
import api from '../api/axios';
import { Link } from 'react-router-dom';

function FeedPage() {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    api.get('/posts')
      .then(res => setPosts(res.data))
      .catch(err => console.error('Error fetching posts:', err));
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h2>Global Feed</h2>
      {posts.map(post => (
        <div key={post.id} style={{ border: '1px solid #ccc', padding: 10, marginBottom: 10 }}>
          <p>
            <strong>
              <Link to={`/profile/${post.userId}`}>{post.username}</Link>
            </strong>
          </p>
          <p>{post.content}</p>
          <small>{new Date(post.createdAt).toLocaleString()}</small>
        </div>
      ))}
    </div>
  );
}

export default FeedPage;
