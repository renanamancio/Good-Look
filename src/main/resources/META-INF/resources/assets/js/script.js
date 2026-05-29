// Funções globais para serem chamadas via onclick
function adicionarAoCarrinho(produtoId) {
    let cart = JSON.parse(localStorage.getItem('cart') || '[]');
    cart.push(produtoId);
    localStorage.setItem('cart', JSON.stringify(cart));
    
    atualizarContadorCarrinho();
    alert('Produto adicionado ao carrinho!');
}

function atualizarContadorCarrinho() {
    let cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const cartLink = document.querySelector('.client-nav a[href*="carrinho.html"]');
    if(cartLink) cartLink.innerHTML = `Carrinho (${cart.length})`;
}

async function logout() {
    try {
        await fetch('/api/auth/logout', { method: 'POST' });
        localStorage.removeItem('user');
        localStorage.removeItem('cart');
        window.location.href = '../index.html';
    } catch (error) {
        console.error('Erro ao deslogar:', error);
    }
}

function removerDoCarrinho(produtoId) {
    let cart = JSON.parse(localStorage.getItem('cart') || '[]');
    const index = cart.indexOf(produtoId);
    if (index > -1) {
        cart.splice(index, 1);
    }
    localStorage.setItem('cart', JSON.stringify(cart));
    
    if (window.location.pathname.includes('carrinho.html')) {
        renderizarCarrinho();
    }
    atualizarContadorCarrinho();
}

function removerItem(itemId) {
    const itemElement = document.getElementById(itemId);
    if (itemElement) {
        // Aplica uma transição suave de fade-out antes de remover o elemento do DOM
        itemElement.style.opacity = '0';
        itemElement.style.transform = 'translateY(10px)';

        setTimeout(() => {
            itemElement.remove();

            // Verifica se ainda existem itens no grid
            const wishlistGrid = document.getElementById('wishlistGrid');
            if (wishlistGrid) {
                const remainingItems = wishlistGrid.querySelectorAll('.luxury-product-card');
                if (remainingItems.length === 0) {
                    wishlistGrid.style.display = 'none';
                    const emptyMessage = document.getElementById('emptyWishlistMessage');
                    if (emptyMessage) emptyMessage.style.display = 'flex';
                }
            }
        }, 400); // Tempo casado com a animação de transição média do CSS
    }
}

function removerItemCarrinho(itemId) {
    const itemElement = document.getElementById(itemId);
    if (itemElement) {
        // Transição suave de fade-out
        itemElement.style.opacity = '0';
        itemElement.style.transform = 'translateY(10px)';

        setTimeout(() => {
            itemElement.remove();

            // Verifica se ainda existem itens no carrinho
            const cartItemsContainer = document.getElementById('cartItemsContainer');
            if (cartItemsContainer) {
                const remainingItems = cartItemsContainer.querySelectorAll('.cart-item');

                if (remainingItems.length === 0) {
                    // Esconde a área de resumo do pedido
                    const orderSummary = document.getElementById('orderSummaryWrapper');
                    if(orderSummary) orderSummary.style.display = 'none';

                    // Exibe a mensagem de carrinho vazio
                    const emptyMessage = document.getElementById('emptyCartMessage');
                    if (emptyMessage) emptyMessage.style.display = 'flex';

                    // Atualiza o contador no header (se existir)
                    const cartLink = document.querySelector('.client-nav a[href*="carrinho.html"]');
                    if(cartLink) cartLink.innerHTML = 'Carrinho (0)';
                }
            }
        }, 400); // Sincronizado com a transição média
    }
}

document.addEventListener("DOMContentLoaded", () => {
    // Verifica sessão
    const user = JSON.parse(localStorage.getItem('user'));
    const isAuthPage = window.location.pathname.endsWith('index.html') || window.location.pathname.includes('/auth/');
    
    if (!user && !isAuthPage) {
        window.location.href = '/index.html';
    }

    atualizarContadorCarrinho();

    // 1. Animação suave de entrada (Reveal)
    const revealElements = document.querySelectorAll('.reveal');
    setTimeout(() => {
        revealElements.forEach(el => {
            el.classList.add('visible');
        });
    }, 100);

    // 2. Lógica para alternar campos Cliente/Admin
    const roleRadios = document.querySelectorAll('input[name="role"]');
    const clientFields = document.getElementById('client-fields');

    // Só executa se estivermos na página de cadastro (onde esses elementos existem)
    if (clientFields && roleRadios.length > 0) {
        roleRadios.forEach(radio => {
            radio.addEventListener('change', (e) => {
                if (e.target.value === 'ADMIN') {
                    clientFields.classList.add('hidden-fields');
                    const cpfInput = document.getElementById('cpf');
                    if (cpfInput) cpfInput.required = false;
                } else {
                    clientFields.classList.remove('hidden-fields');
                    const cpfInput = document.getElementById('cpf');
                    if (cpfInput) cpfInput.required = true;
                }
            });
        });
    }

    // 3. Integração com a API do ViaCEP
    const cepInput = document.getElementById('cep');

    if (cepInput) {
        cepInput.addEventListener('blur', async (e) => {
            // Remove tudo que não for número do CEP
            let cep = e.target.value.replace(/\D/g, '');

            if (cep.length === 8) {
                try {
                    // Feedback visual sutil (borda dourada enquanto carrega)
                    cepInput.style.borderBottomColor = 'var(--color-accent)';

                    const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
                    const data = await response.json();

                    if (!data.erro) {
                        // Preenche os campos automaticamente
                        const logradouro = document.getElementById('logradouro');
                        const bairro = document.getElementById('bairro');
                        const cidade = document.getElementById('cidade');
                        const uf = document.getElementById('uf');
                        const numero = document.getElementById('numero');

                        if (logradouro) logradouro.value = data.logradouro;
                        if (bairro) bairro.value = data.bairro;
                        if (cidade) cidade.value = data.localidade;
                        if (uf) uf.value = data.uf;

                        // Foca no campo de número para o usuário continuar digitando
                        if (numero) numero.focus();
                    } else {
                        alert("CEP não encontrado.");
                        limparCamposEndereco();
                    }
                } catch (error) {
                    console.error("Erro ao buscar CEP:", error);
                } finally {
                    // Volta a borda ao normal
                    cepInput.style.borderBottomColor = '';
                }
            }
        });
    }

    function limparCamposEndereco() {
        const fields = ['logradouro', 'bairro', 'cidade', 'uf'];
        fields.forEach(f => {
            const el = document.getElementById(f);
            if (el) el.value = '';
        });
    }

    // Prevenção do form apenas para demonstração do front-end
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, senha: password })
                });

                if (response.ok) {
                    const data = await response.json();
                    localStorage.setItem('user', JSON.stringify(data));
                    if (data.role === 'ADMIN') {
                        window.location.href = 'admin/home.html';
                    } else {
                        window.location.href = 'cliente/home.html';
                    }
                } else {
                    const error = await response.json();
                    alert(error.message || 'Erro ao realizar login');
                }
            } catch (error) {
                console.error('Erro:', error);
                alert('Erro de conexão com o servidor');
            }
        });
    }

    // Carregar produtos na home do cliente
    const productGrid = document.querySelector('.storefront-grid');
    if (productGrid && window.location.pathname.includes('cliente/home.html')) {
        carregarProdutos();
    }

    async function carregarProdutos() {
        try {
            const response = await fetch('/api/produtos');
            const produtos = await response.json();

            productGrid.innerHTML = ''; // Limpa o grid

            produtos.forEach(produto => {
                const card = `
                    <article class="luxury-product-card reveal visible">
                        <div class="card-image-zone" style="background-image: url('${produto.imagem}'); background-size: cover; background-position: center;">
                            <span class="badge-exclusive">Novidade</span>
                        </div>
                        <div class="card-details">
                            <h2 class="card-title">${produto.nome}</h2>
                            <p class="card-description" style="font-size: 0.8rem; color: var(--color-text-dim); margin-bottom: 0.5rem;">${produto.descricao}</p>
                            <span class="card-price">${new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(produto.preco)}</span>
                            <button class="btn-primary btn-add-cart" onclick="adicionarAoCarrinho('${produto.id}')">Adicionar ao Carrinho</button>
                        </div>
                    </article>
                `;
                productGrid.innerHTML += card;
            });
        } catch (error) {
            console.error('Erro ao carregar produtos:', error);
        }
    }

    // Lógica para clique na zona de upload de produto
    const uploadZone = document.getElementById('uploadZone');
    const fileInput = document.getElementById('imagem'); // Alinhado com o novo ID

    if (uploadZone && fileInput) {
        uploadZone.addEventListener('click', () => {
            fileInput.click();
        });

        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                const fileName = e.target.files[0].name;
                const textElement = uploadZone.querySelector('.upload-text');
                if (textElement) {
                    textElement.innerHTML = `Peça selecionada: <span style="color: var(--color-accent); font-weight: 400;">${fileName}</span>`;
                }
            }
        });
    }
});
 }).format(subtotal);
        if (subtotalEl) subtotalEl.innerText = formattedSubtotal;
        if (totalEl) totalEl.innerText = formattedSubtotal;
    }

    async function carregarProdutos() {
        try {
            const response = await fetch('/api/produtos');
            const produtos = await response.json();

            productGrid.innerHTML = ''; // Limpa o grid

            produtos.forEach(produto => {
                const card = `
                    <article class="luxury-product-card reveal visible">
                        <div class="card-image-zone" style="background-image: url('${produto.imagem}'); background-size: cover; background-position: center;">
                            <span class="badge-exclusive">Novidade</span>
                        </div>
                        <div class="card-details">
                            <h2 class="card-title">${produto.nome}</h2>
                            <p class="card-description" style="font-size: 0.8rem; color: var(--color-text-dim); margin-bottom: 0.5rem;">${produto.descricao}</p>
                            <span class="card-price">${new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(produto.preco)}</span>
                            <button class="btn-primary btn-add-cart" onclick="adicionarAoCarrinho('${produto.id}')">Adicionar ao Carrinho</button>
                        </div>
                    </article>
                `;
                productGrid.innerHTML += card;
            });
        } catch (error) {
            console.error('Erro ao carregar produtos:', error);
        }
    }

    // Lógica para clique na zona de upload de produto
    const uploadZone = document.getElementById('uploadZone');
    const fileInput = document.getElementById('imagem'); // Alinhado com o novo ID

    if (uploadZone && fileInput) {
        uploadZone.addEventListener('click', () => {
            fileInput.click();
        });

        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                const fileName = e.target.files[0].name;
                const textElement = uploadZone.querySelector('.upload-text');
                if (textElement) {
                    textElement.innerHTML = `Peça selecionada: <span style="color: var(--color-accent); font-weight: 400;">${fileName}</span>`;
                }
            }
        });
    }
});
