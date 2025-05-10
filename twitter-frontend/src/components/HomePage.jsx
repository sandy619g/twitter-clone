import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/axios';

function HomePage() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    api.get('/users').then(res => setUsers(res.data));
  }, []);

  return (
    <div className="container">
      <h2>All Users</h2>
      {users.map(user => (
        <div className="user-card" key={user.id}>
          <Link to={`/profile/${user.id}`}>
            <strong>{user.username}</strong>
          </Link>
        </div>
      ))}
    </div>
  );
}

export default HomePage;
