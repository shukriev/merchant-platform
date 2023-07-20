import React, { useEffect, useState } from 'react';
import { Link, useHistory } from 'react-router-dom';
import axios from 'axios';

const TransactionComponent = () => {
  const history = useHistory();

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
      <h1>Merchant Transactions</h1>
      <Link to="/create-transaction">
        <button>Create Transaction</button>
      </Link>
      <div>
      <div>
      <h2>Transaction Table</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Transaction Type</th>
            <th>Merchant ID</th>
            <th>Merchant Email</th>
            <th>Merchant Name</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Customer Email</th>
            <th>Customer Phone</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((transaction) => (
            <tr key={transaction.id}>
              <td>{transaction.id}</td>
              <td>{transaction.transactionType}</td>
              <td>{transaction.merchant.id}</td>
              <td>{transaction.merchant.email}</td>
              <td>{transaction.merchant.name}</td>
              <td>{transaction.amount}</td>
              <td>{transaction.status}</td>
              <td>{transaction.customerEmail}</td>
              <td>{transaction.customerPhone}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>     
     </div>
    </div>
  )
};

export default TransactionComponent;