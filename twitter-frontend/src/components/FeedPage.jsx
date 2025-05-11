import { useEffect, useState } from 'react';
import api from '../api/axios';
import { Link } from 'react-router-dom';

function FeedPage() {
  const [posts, setPosts] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get('/api/posts')
      .then(res => {
        const data = res.data;
        if (Array.isArray(data)) {
          setPosts(data);
        } else {
          console.warn('Expected array but got:', data);
          setPosts([]);
        }
        setLoading(false);
      })
      .catch(err => {
        console.error('Error fetching posts:', err);
        setError('Failed to load posts.');
        setLoading(false);
      });
  }, []);

  if (loading) return <p>Loading posts...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div style={{ padding: 20 }}>
      <h2>Global Feed</h2>
      {posts.length === 0 ? (
        <p>No posts found.</p>
      ) : (
        posts.map(post => (
          <div key={post.id} style={{ border: '1px solid #ccc', padding: 10, marginBottom: 10 }}>
            <p>
              <strong>
                <Link to={`/profile/${post.userId}`}>{post.username}</Link>
              </strong>
            </p>
            <p>{post.content}</p>
            <small>{new Date(post.createdAt).toLocaleString()}</small>
          </div>
        ))
      )}
    </div>
  );
}

export default FeedPage;
