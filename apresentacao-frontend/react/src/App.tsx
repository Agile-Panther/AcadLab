import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout/Layout';
import IntegralizacaoDashboard from './pages/integralizacao/IntegralizacaoDashboard';
import IntegralizacaoEstudante from './pages/integralizacao/IntegralizacaoEstudante';
import IntegralizacaoAnalise from './pages/integralizacao/IntegralizacaoAnalise';
import IntegralizacaoResultado from './pages/integralizacao/IntegralizacaoResultado';
import IntegralizacaoAprovacao from './pages/integralizacao/IntegralizacaoAprovacao';
import IntegralizacaoColacao from './pages/integralizacao/IntegralizacaoColacao';
import './App.css';

function Placeholder({ titulo }: { titulo: string }) {
  return (
    <div style={{ padding: '2rem' }}>
      <h1>{titulo}</h1>
      <p>Funcionalidade em desenvolvimento.</p>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Navigate to="/integralizacao" replace />} />

          <Route path="/gestao-curricular" element={<Placeholder titulo="Gestão Curricular" />} />
          <Route path="/periodo-letivo" element={<Placeholder titulo="Período Letivo" />} />
          <Route path="/oferta-turmas" element={<Placeholder titulo="Oferta de Turmas" />} />
          <Route path="/matricula" element={<Placeholder titulo="Matrícula" />} />
          <Route path="/gestao-pedagogica" element={<Placeholder titulo="Gestão Pedagógica" />} />
          <Route path="/historico-academico" element={<Placeholder titulo="Histórico Acadêmico" />} />
          <Route path="/secretaria-virtual" element={<Placeholder titulo="Secretaria Virtual" />} />
          <Route path="/ativ-complementares" element={<Placeholder titulo="Ativ. Complementares" />} />
          <Route path="/gestao-financeira" element={<Placeholder titulo="Gestão Financeira" />} />
          <Route path="/estagios" element={<Placeholder titulo="Estágios" />} />

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
