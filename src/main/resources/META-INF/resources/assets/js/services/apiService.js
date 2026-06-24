/**
 * Serviço exclusivo para chamadas Fetch à API.
 * Não manipula o DOM.
 */
import CONFIG from './config.js';

export const ApiService = {
    /**
     * Realiza uma requisição genérica.
     */
    async request(endpoint, options = {}) {
        const url = `${CONFIG.API_BASE_URL}${endpoint}`;
        
        const defaultHeaders = {
            'Authorization': `Bearer ${localStorage.getItem('AUTH_TOKEN')}`
        };

        if (!(options.body instanceof FormData)) {
            defaultHeaders['Content-Type'] = 'application/json';
        }

        const config = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                
                // Trata validações estruturadas do Quarkus/Hibernate Validator
                if (errorData.parameterViolations && errorData.parameterViolations.length > 0) {
                    const messages = errorData.parameterViolations.map(v => v.message).join('\n');
                    throw new Error(messages);
                }
                if (errorData.violations && errorData.violations.length > 0) {
                    const messages = errorData.violations.map(v => v.message).join('\n');
                    throw new Error(messages);
                }
                
                throw new Error(errorData.message || `Erro HTTP: ${response.status}`);
            }

            // Para DELETE ou respostas sem corpo
            if (response.status === 204) return null;

            return await response.json();
        } catch (error) {
            console.error(`Falha na requisição para ${url}:`, error);
            throw error;
        }
    },

    // Métodos utilitários
    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    },

    post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    postMultipart(endpoint, formData) {
        return this.request(endpoint, {
            method: 'POST',
            body: formData
        });
    },

    put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
};
