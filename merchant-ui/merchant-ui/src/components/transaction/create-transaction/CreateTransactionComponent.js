import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useHistory } from 'react-router-dom';

const CreateTransaction = () => {
    const history = useHistory();
    const [transactions, setTransactions] = useState([]);
    const [transactionType, setTransactionType] = useState('Authorize');
    const [amount, setAmount] = useState('');
    const [transactionStatus, setTransactionStatus] = useState('Approved');
    const [customerEmail, setCustomerEmail] = useState('');
    const [customerPhone, setCustomerPhone] = useState('');
    const [referenceId, setReferenceId] = useState('');
    
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

  const handleSubmit = async (event) => {
    event.preventDefault();
      
    try {
        const transactionData = {
            transactionType: transactionType,
            amount: amount,
            status: transactionStatus,
            customerEmail: customerEmail,
            customerPhone: customerPhone,
            referenceId: referenceId,
        };

        const response = await axios.post('http://localhost:8080/transactions', transactionData, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
        console.log('Transaction created:', response.data);
        history.push('/transactions');

      } catch (error) {
        console.error('Failed to create transaction:', error);
      }
  };

  const handleTransactionTypeChange = (event) => {
    setTransactionType(event.target.value);
  };
  const handleTransactionStatusChange = (event) => {
    setTransactionStatus(event.target.value);
  };

  const handleAmountChange = (event) => {
    setAmount(event.target.value);
  };

  const handleCustomerEmailChange = (event) => {
    setCustomerEmail(event.target.value);
  };

  const handleCustomerPhoneChange = (event) => {
    setCustomerPhone(event.target.value);
  };

  const handleReferenceChange = (event) => {
    setReferenceId(event.target.value);
  };

  const transactionTypeDropDown = ['Authorize', 'Charge', 'Refund', 'Reversal'];
  const transactionStatusDropDown = ['Approved', 'Reversed', 'Refunded', 'Error'];

  return (
    <div>
      <h1>Create Transaction</h1>
      <form onSubmit={handleSubmit}>
      <label htmlFor="transactionType">Transaction Type:</label>
      <select value={transactionType} onChange={handleTransactionTypeChange}>
        {transactionTypeDropDown.map((tt) => (
          <option key={tt} value={tt}>
            {tt.toUpperCase()}
          </option>
        ))}
      </select>
      <br/>
      <label htmlFor="amount">Amount:</label>
      <input type="number" id="amount" value={amount} onChange={handleAmountChange}/>
      <br/>
      <label htmlFor="transactionStatus">Transaction Status:</label>
      <select value={transactionStatus} onChange={(e) => handleTransactionStatusChange}>
        {transactionStatusDropDown.map((ts) => (
          <option key={ts} value={ts}>
            {ts.toUpperCase()}
          </option>
        ))}
      </select>
      <br/>
      <label htmlFor="customerEmail">Customer Email:</label>
      <input type="text" id="customerEmail" value={customerEmail} onChange={handleCustomerEmailChange}/>
      <br/>
      <label htmlFor="customerPhone">Customer Phone:</label>
      <input type="text" id="customerPhone" value={customerPhone} onChange={handleCustomerPhoneChange}/>
      <br/>
      <select value={referenceId} onChange={handleReferenceChange}>
        {transactions.map((ts) => (
          <option key={ts.id} value={ts.id}>
            {ts.id}
          </option>
        ))}
      </select>

      <br/>
      <button type="submit">Submit</button> 
      </form>
    </div>
  );
};

export default CreateTransaction;