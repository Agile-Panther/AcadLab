import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import MinhasSolicitacoes from './pages/secretariavirtual/MinhasSolicitacoes';
import NovaSolicitacao from './pages/secretariavirtual/NovaSolicitacao';
import DetalhesSolicitacao from './pages/secretariavirtual/DetalhesSolicitacao';
import AnaliseSecretaria from './pages/secretariavirtual/AnaliseSecretaria';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />
          <Route path="/secretaria-virtual" element={<MinhasSolicitacoes />} />
          <Route path="/secretaria-virtual/nova" element={<NovaSolicitacao />} />
          <Route path="/secretaria-virtual/:id" element={<DetalhesSolicitacao />} />
          <Route path="/secretaria-virtual/:id/complementar" element={<DetalhesSolicitacao />} />
          <Route path="/secretaria-virtual/analise" element={<AnaliseSecretaria />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
