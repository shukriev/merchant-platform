import './App.css';
import React, { useEffect, useState }  from 'react';
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom'
import LoginComponent from './components/login/LoginComponent';
import TransactionComponent from './components/transaction/list-transaction/ListTransactionComponent';
import CreateTransaction from './components/transaction/create-transaction/CreateTransactionComponent';
import AdminListMerchantComponent from './components/admin/merchant/AdminListMerchantComponent';
import { useHistory } from 'react-router-dom';

import axios from 'axios';


const App = () => {
  var history = useHistory();
  axios.defaults.baseURL = '/';
  const token = localStorage.getItem('token')

  //TODO has to check if token is still valid
  const isLoggedIn = token !== undefined && token !== null;
  var isAdmin = false;

  const [loggedMerchant, setLoggedMerchant] = useState([]);

  useEffect(() => {
    const fetchMerchant = async () => {
      try {
        const response = await axios.get('http://localhost:8080/auth/details', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        setLoggedMerchant(response.data);

      } catch (error) {
        console.error('Failed to fetch logged merchant:', error);
      }
    };

    fetchMerchant();
  }, [token]);



  return (
    <Router>
      <Switch>
          {/* Redirect to the appropriate route based on isLoggedIn and isAdmin */}
          {isLoggedIn && isAdmin ? (
            <Redirect exact from="/" to="/admin/merchant/list" />
          ) : isLoggedIn ? (
            <Redirect exact from="/" to="/transactions" />
          ) : (
            <Redirect exact from="/" to="/login" />
          )}

          {/* Route for the AdminListMerchantComponent */}
          <Route exact path="/admin/merchant/list" component={AdminListMerchantComponent} />

          {/* Route for the LoginComponent */}
          <Route exact path="/login" component={LoginComponent} />

          {/* Route for the TransactionComponent */}
          <Route exact path="/transactions" component={TransactionComponent} />

          {/* Route for the CreateTransaction */}
          <Route exact path="/create-transaction" component={CreateTransaction} />

          {/* If no matching route is found, show a NotFoundPage or redirect to / */}
          <Redirect to="/" />
        </Switch>
    </Router>

  );
};

export default App;
