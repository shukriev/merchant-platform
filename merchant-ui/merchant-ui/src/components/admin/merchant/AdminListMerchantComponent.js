import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

const AdminListMerchantComponent = () => {
      const [transactions, setTransactions] = useState([]);

      const token = localStorage.getItem('token');
      useEffect(() => {
        const fetchTransactions = async () => {
          try {
            const response = await axios.get('http://localhost:8080/transactions', {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
    
            setTransactions(response.data);

            
          } catch (error) {
            console.error('Failed to fetch transactions:', error);
          }
        };
    
        fetchTransactions();
      }, [token]);
    
  return (
    <div>
      <h1>Merchant Admin Console</h1>
    </div>
  )
};

export default AdminListMerchantComponent;