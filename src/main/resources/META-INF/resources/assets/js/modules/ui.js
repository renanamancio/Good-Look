/**
 * Módulo exclusivo para manipulação do DOM.
 * Não realiza requisições HTTP.
 */
export const UI = {
    /**
     * Extrai dados de um formulário e retorna como objeto JS.
     */
    getFormData(formId) {
        const form = document.getElementById(formId);
        if (!form) return null;
        
        const formData = new FormData(form);
        const data = {};
        for (let [key, value] of formData.entries()) {
            data[key] = value;
        }
        return data;
    },

    /**
     * Limpa os campos de um formulário.
     */
    resetForm(formId) {
        const form = document.getElementById(formId);
        if (form) form.reset();
    },

    /**
     * Exibe um alerta de sucesso nativo (pode ser evoluído para Toast).
     */
    showSuccess(message) {
        alert(`✅ Sucesso: ${message}`);
    },

    /**
     * Exibe um alerta de erro nativo (pode ser evoluído para Toast).
     */
    showError(message) {
        alert(`❌ Erro: ${message}`);
    },

    /**
     * Confirmação antes de ação destrutiva.
     */
    confirmAction(message) {
        return confirm(message);
    },

    /**
     * Remove um elemento do DOM pelo ID (ex: remover linha da tabela).
     */
    removeElement(elementId) {
        const el = document.getElementById(elementId);
        if (el) el.remove();
    },

    /**
     * Renderiza linhas de produtos dinamicamente.
     */
    renderProdutosTable(produtos) {
        const tbody = document.getElementById('tabelaProdutos');
        if (!tbody) return;

        tbody.innerHTML = '';
        produtos.forEach(p => {
            const row = `
                <tr id="row-produto-${p.id}">
                    <td>
                      <div class="product-profile-cell">
                        <div class="product-thumb" style="background-image: url('${p.imagem}'); background-size: cover;"></div>
                        <div class="product-info">
                          <span class="product-name">${p.nome}</span>
                        </div>
                      </div>
                    </td>
                    <td class="product-category">${p.url}</td>
                    <td class="product-price">R$ ${p.preco.toFixed(2)}</td>
                    <td class="product-stock">${p.quantidade} unid.</td>
                    <td class="table-actions">
                        <button class="action-link delete" data-action="delete-produto" data-id="${p.id}">Excluir</button>
                    </td>
                </tr>
            `;
            tbody.innerHTML += row;
        });
    },

    /**
     * Renderiza linhas de usuários dinamicamente.
     */
    renderUsuariosTable(usuarios) {
        const tbody = document.getElementById('tabelaUsuarios');
        if (!tbody) return;

        tbody.innerHTML = '';
        usuarios.forEach(u => {
            const row = `
                <tr id="row-usuario-${u.id}">
                    <td class="user-email">${u.email}</td>
                    <td>
                        <span class="badge-luxury">${u.role}</span>
                    </td>
                    <td class="table-actions">
                        <button class="action-link delete" data-action="delete-usuario" data-id="${u.id}">Excluir</button>
                    </td>
                </tr>
            `;
            tbody.innerHTML += row;
        });
    }
};
