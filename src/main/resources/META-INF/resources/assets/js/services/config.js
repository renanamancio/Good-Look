/**
 * Centralização das URLs da API.
 * Detecta se o ambiente é local ou produção.
 */
const CONFIG = {
    // URL base da API
    API_BASE_URL: window.location.hostname === 'localhost' 
        ? 'http://localhost:8080' 
        : '/api',
    
    // Endpoints específicos
    ENDPOINTS: {
        PRODUTOS: '/admin/produtos/api',
        USUARIOS: '/admin/usuarios/api',
        AUTH: '/auth',
        CARRINHO: '/api/carrinho',
        PEDIDOS: '/api/pedidos'
    }
};

export default CONFIG;
