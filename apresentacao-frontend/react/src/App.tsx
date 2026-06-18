import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Home from './pages/Home';
import MinhasSolicitacoes from './pages/secretariavirtual/MinhasSolicitacoes';
import AnaliseSecretaria from './pages/secretariavirtual/AnaliseSecretaria';
import IntegralizacaoDashboard from './pages/integralizacao/IntegralizacaoDashboard';
import IntegralizacaoEstudante from './pages/integralizacao/IntegralizacaoEstudante';
import IntegralizacaoAnalise from './pages/integralizacao/IntegralizacaoAnalise';
import IntegralizacaoResultado from './pages/integralizacao/IntegralizacaoResultado';
import IntegralizacaoAprovacao from './pages/integralizacao/IntegralizacaoAprovacao';
import IntegralizacaoColacao from './pages/integralizacao/IntegralizacaoColacao';
import './App.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Home />} />
          <Route path="/secretaria-virtual" element={<MinhasSolicitacoes />} />
          <Route path="/secretaria-virtual/analise" element={<AnaliseSecretaria />} />
          <Route path="/integralizacao" element={<IntegralizacaoDashboard />} />
          <Route path="/integralizacao/estudante/:estudanteId" element={<IntegralizacaoEstudante />} />
          <Route path="/integralizacao/:id/analise" element={<IntegralizacaoAnalise />} />
          <Route path="/integralizacao/:id/resultado" element={<IntegralizacaoResultado />} />
          <Route path="/integralizacao/:id/aprovacao" element={<IntegralizacaoAprovacao />} />
          <Route path="/integralizacao/:id/colacao" element={<IntegralizacaoColacao />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
