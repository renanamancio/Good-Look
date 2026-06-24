/**
 * Orquestrador da Aplicação Front-end.
 * Une a interface (UI) com as requisições HTTP (ApiService).
 */
import CONFIG from './services/config.js';
import { ApiService } from './services/apiService.js';
import { UI } from './modules/ui.js';

document.addEventListener('DOMContentLoaded', () => {

    // ==========================================
    // UI: Animações de Entrada (Reveal)
    // ==========================================
    const revealElements = document.querySelectorAll('.reveal');
    const revealObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target);
            }
        });
    }, { rootMargin: '0px 0px -50px 0px' });

    revealElements.forEach(el => revealObserver.observe(el));

    // Fallback de segurança para garantir a visibilidade
    setTimeout(() => {
        revealElements.forEach(el => el.classList.add('visible'));
    }, 100);

    // ==========================================
    // SEGURANÇA & REDIRECIONAMENTOS (CLIENT-SIDE)
    // ==========================================
    const path = window.location.pathname;
    const token = localStorage.getItem('AUTH_TOKEN');
    const role = localStorage.getItem('USER_ROLE');

    // Validação de rota de login/cadastro
    if (path === '/' || path === '/auth/cadastro' || path.endsWith('index.html') || path.endsWith('cadastro.html')) {
        if (token) {
            if (role === 'ADMIN') {
                window.location.href = '/admin/home';
            } else {
                window.location.href = '/cliente/home';
            }
        }
    } else if (path.startsWith('/cliente') || path.startsWith('/admin')) {
        // Rotas protegidas
        if (!token) {
            window.location.href = '/';
        }
    }

    // Função de logout global
    window.logout = async function() {
        try {
            await ApiService.post('/auth/logout');
        } catch(e) {}
        localStorage.clear();
        window.location.href = '/';
    };

    // ==========================================
    // MÓDULO: AUTENTICAÇÃO (LOGIN)
    // ==========================================
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await ApiService.post(`${CONFIG.ENDPOINTS.AUTH}/login`, { email, senha: password });
                
                localStorage.setItem('AUTH_TOKEN', response.token);
                localStorage.setItem('USER_ROLE', response.user.role);
                localStorage.setItem('USER_EMAIL', response.user.email);

                UI.showSuccess('Login realizado com sucesso!');
                
                if (response.user.role === 'ADMIN') {
                    window.location.href = '/admin/home';
                } else {
                    window.location.href = '/cliente/home';
                }
            } catch (error) {
                UI.showError(error.message || 'E-mail ou senha inválidos');
            }
        });
    }

    // ==========================================
    // MÓDULO: CADASTRO DE CONTA
    // ==========================================
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const isAdmin = document.getElementById('isAdmin').checked;

            const registerData = {
                nome: name,
                email: email,
                senha: password,
                administrador: isAdmin
            };

            try {
                const response = await ApiService.post(`${CONFIG.ENDPOINTS.AUTH}/register`, registerData);
                
                localStorage.setItem('AUTH_TOKEN', response.token);
                localStorage.setItem('USER_ROLE', response.user.role);
                localStorage.setItem('USER_EMAIL', response.user.email);

                UI.showSuccess('Cadastro realizado com sucesso!');
                
                if (response.user.role === 'ADMIN') {
                    window.location.href = '/admin/home';
                } else {
                    window.location.href = '/cliente/home';
                }
            } catch (error) {
                UI.showError(error.message || 'Falha no cadastro. Verifique os dados inseridos.');
            }
        });
    }

    // ==========================================
    // MÓDULO: ACERVO (HOME DO CLIENTE)
    // ==========================================
    const storefrontGrid = document.querySelector('.storefront-grid');
    if (storefrontGrid && path.includes('/cliente/home')) {
        
        const carregarLoja = async () => {
            atualizarContadorCarrinho();
            try {
                const produtos = await ApiService.get(CONFIG.ENDPOINTS.PRODUTOS);
                storefrontGrid.innerHTML = '';
                
                if (produtos.length === 0) {
                    storefrontGrid.innerHTML = '<p class="empty-text">Nenhum produto cadastrado no momento.</p>';
                    return;
                }

                produtos.forEach(p => {
                    const card = `
                        <article class="luxury-product-card reveal visible">
                            <a href="/cliente/produto/${p.id}" class="luxury-card-link-wrapper">
                                <div class="card-image-zone" style="background-image: url('${p.imagem}'); background-size: cover; background-position: center; height: 350px;">
                                    <span class="badge-exclusive">Disponível</span>
                                </div>
                            </a>
                            <div class="card-details">
                                <h2 class="card-title">${p.nome}</h2>
                                <span class="card-price">R$ ${p.preco.toFixed(2)}</span>
                                <button class="btn-primary btn-add-cart" data-id="${p.id}">Adicionar ao Carrinho</button>
                            </div>
                        </article>
                    `;
                    storefrontGrid.innerHTML += card;
                });
            } catch (error) {
                console.error('Erro ao carregar acervo:', error);
                storefrontGrid.innerHTML = '<p class="empty-text">Erro ao carregar produtos. Tente novamente mais tarde.</p>';
            }
        };

        // Escuta cliques dinâmicos nos botões de adicionar ao carrinho
        storefrontGrid.addEventListener('click', async (e) => {
            if (e.target.classList.contains('btn-add-cart')) {
                const id = e.target.dataset.id;
                try {
                    await ApiService.post(`${CONFIG.ENDPOINTS.CARRINHO}/itens?produtoId=${id}&quantidade=1`);
                    UI.showSuccess('Produto adicionado ao carrinho!');
                    atualizarContadorCarrinho();
                } catch (error) {
                    UI.showError(error.message);
                }
            }
        });

        carregarLoja();
    }

    // ==========================================
    // MÓDULO: DETALHES DO PRODUTO
    // ==========================================
    const productDetailsContainer = document.getElementById('productDetailsContainer');
    if (productDetailsContainer) {
        const productId = productDetailsContainer.dataset.productId;
        
        const carregarDetalhes = async () => {
            atualizarContadorCarrinho();
            try {
                const p = await ApiService.get(`${CONFIG.ENDPOINTS.PRODUTOS}/${productId}`);
                
                document.querySelector('.product-detail-title').innerText = p.nome;
                document.querySelector('.product-detail-price').innerText = `R$ ${p.preco.toFixed(2)}`;
                document.querySelector('.product-description p').innerText = p.descricao;
                
                const metaValueStock = document.querySelector('.meta-value');
                if (metaValueStock) {
                    metaValueStock.innerText = `${p.quantidade} unidades em stock`;
                }

                const gallery = document.querySelector('.main-image-placeholder');
                if (gallery) {
                    gallery.innerHTML = '';
                    gallery.style.backgroundImage = `url('${p.imagem}')`;
                    gallery.style.backgroundSize = 'cover';
                    gallery.style.backgroundPosition = 'center';
                }

                // Configura limites do input de quantidade
                const qtyInput = document.querySelector('.quantity-selector input[type=number]');
                if (qtyInput) {
                    qtyInput.max = p.quantidade;
                }
            } catch (err) {
                console.error('Erro ao obter detalhes do produto:', err);
            }
        };

        // Clique para adicionar ao carrinho a partir dos detalhes
        const addCartBtn = document.querySelector('.product-actions .btn-primary');
        if (addCartBtn) {
            addCartBtn.addEventListener('click', async () => {
                const qtyInput = document.querySelector('.quantity-selector input[type=number]');
                const qty = qtyInput ? parseInt(qtyInput.value) : 1;
                try {
                    await ApiService.post(`${CONFIG.ENDPOINTS.CARRINHO}/itens?produtoId=${productId}&quantidade=${qty}`);
                    UI.showSuccess('Produto adicionado ao carrinho!');
                    atualizarContadorCarrinho();
                } catch (error) {
                    UI.showError(error.message);
                }
            });
        }

        carregarDetalhes();
    }

    // ==========================================
    // MÓDULO: GERENCIAMENTO DE CARRINHO
    // ==========================================
    const cartItemsContainer = document.getElementById('cartItemsContainer');
    if (cartItemsContainer && path.includes('/cliente/carrinho')) {
        
        const renderCarrinho = async () => {
            try {
                const carrinho = await ApiService.get(CONFIG.ENDPOINTS.CARRINHO);
                
                // Atualiza header
                const qtyTotal = carrinho.quantidadeTotal || 0;
                const headerCartLink = document.getElementById('cartHeaderLink');
                if (headerCartLink) {
                    headerCartLink.innerText = `Carrinho (${qtyTotal})`;
                }

                // Resumo do Pedido
                const summaryWrapper = document.getElementById('orderSummaryWrapper');
                const emptyMsg = document.getElementById('emptyCartMessage');

                if (carrinho.itens.length === 0) {
                    if (summaryWrapper) summaryWrapper.style.display = 'none';
                    if (emptyMsg) emptyMsg.style.display = 'block';
                    
                    // Limpa itens antigos
                    const items = cartItemsContainer.querySelectorAll('.cart-item');
                    items.forEach(el => el.remove());
                } else {
                    if (summaryWrapper) summaryWrapper.style.display = 'block';
                    if (emptyMsg) emptyMsg.style.display = 'none';

                    // Limpa e renderiza itens
                    const items = cartItemsContainer.querySelectorAll('.cart-item');
                    items.forEach(el => el.remove());

                    carrinho.itens.forEach(item => {
                        const itemHtml = `
                            <div class="cart-item" id="cart-item-${item.produtoId}">
                                <div class="cart-item-image" style="background-image: url(''); background-size: cover; background-position: center; background-color: #1a1a1a;">
                                </div>
                                <div class="cart-item-details">
                                    <div class="cart-item-header">
                                        <div>
                                            <h3 class="cart-item-title">${item.produtoNome}</h3>
                                        </div>
                                        <span class="cart-item-price">R$ ${item.precoUnitario.toFixed(2)}</span>
                                    </div>
                                    <div class="cart-item-actions">
                                        <div class="quantity-selector" style="height: 40px;">
                                            <button class="qty-btn" data-action="decrement" data-id="${item.produtoId}" data-qty="${item.quantidade}">-</button>
                                            <input type="number" value="${item.quantidade}" readonly>
                                            <button class="qty-btn" data-action="increment" data-id="${item.produtoId}">+</button>
                                        </div>
                                        <button class="btn-remove-text" data-action="remove" data-id="${item.produtoId}">Remover</button>
                                    </div>
                                </div>
                            </div>
                        `;
                        cartItemsContainer.insertAdjacentHTML('afterbegin', itemHtml);
                        
                        // Busca o produto correspondente para recuperar a imagem
                        ApiService.get(`${CONFIG.ENDPOINTS.PRODUTOS}/${item.produtoId}`).then(p => {
                            const imgDiv = document.querySelector(`#cart-item-${item.produtoId} .cart-item-image`);
                            if (imgDiv) {
                                imgDiv.style.backgroundImage = `url('${p.imagem}')`;
                            }
                        }).catch(err => console.error(err));
                    });

                    // Atualiza totais na UI
                    const subtotalEl = document.querySelector('.summary-row .summary-value');
                    const totalEl = document.querySelector('.summary-total-value');
                    if (subtotalEl) subtotalEl.innerText = `R$ ${carrinho.precoTotal.toFixed(2)}`;
                    if (totalEl) totalEl.innerText = `R$ ${carrinho.precoTotal.toFixed(2)}`;
                }
            } catch (error) {
                console.error('Erro ao renderizar carrinho:', error);
            }
        };

        // Ações delegadas no carrinho
        cartItemsContainer.addEventListener('click', async (e) => {
            const action = e.target.dataset.action;
            const id = e.target.dataset.id;
            const qty = parseInt(e.target.dataset.qty);

            if (action === 'decrement') {
                try {
                    if (qty === 1) {
                        await ApiService.delete(`${CONFIG.ENDPOINTS.CARRINHO}/itens/${id}`);
                    } else {
                        // Para decrementar, removemos tudo e adicionamos a nova quantidade
                        await ApiService.delete(`${CONFIG.ENDPOINTS.CARRINHO}/itens/${id}`);
                        await ApiService.post(`${CONFIG.ENDPOINTS.CARRINHO}/itens?produtoId=${id}&quantidade=${qty - 1}`);
                    }
                    renderCarrinho();
                } catch (error) {
                    UI.showError(error.message);
                }
            } else if (action === 'increment') {
                try {
                    await ApiService.post(`${CONFIG.ENDPOINTS.CARRINHO}/itens?produtoId=${id}&quantidade=1`);
                    renderCarrinho();
                } catch (error) {
                    UI.showError(error.message);
                }
            } else if (action === 'remove') {
                try {
                    await ApiService.delete(`${CONFIG.ENDPOINTS.CARRINHO}/itens/${id}`);
                    renderCarrinho();
                } catch (error) {
                    UI.showError(error.message);
                }
            }
        });

        // Botão Finalizar Compra
        const checkoutBtn = document.querySelector('.order-summary-card .btn-primary');
        if (checkoutBtn) {
            checkoutBtn.addEventListener('click', async () => {
                if (UI.confirmAction('Deseja finalizar a compra dos itens do seu carrinho?')) {
                    try {
                        await ApiService.post(`${CONFIG.ENDPOINTS.PEDIDOS}/checkout`);
                        UI.showSuccess('Compra finalizada com sucesso! Seu pedido foi registrado.');
                        window.location.href = '/cliente/home';
                    } catch (error) {
                        UI.showError(error.message);
                    }
                }
            });
        }

        renderCarrinho();
    }

    // ==========================================
    // MÓDULO: GERENCIAMENTO DE PRODUTOS (ADMIN)
    // ==========================================
    const formProduto = document.getElementById('produtoForm');
    if (formProduto) {
        const imagemInput = document.getElementById('imagemFile');
        const previewContainer = document.getElementById('imagePreviewContainer');
        const previewImg = document.getElementById('imagePreview');

        imagemInput.addEventListener('change', () => {
            const file = imagemInput.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    previewImg.src = e.target.result;
                    previewContainer.style.display = 'block';
                };
                reader.readAsDataURL(file);
            } else {
                previewContainer.style.display = 'none';
                previewImg.src = '';
            }
        });

        formProduto.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const submitBtn = formProduto.querySelector('button[type="submit"]');
            const originalBtnText = submitBtn.innerText;
            submitBtn.disabled = true;
            submitBtn.innerText = 'Publicando peça...';

            try {
                const file = imagemInput.files[0];
                if (!file) {
                    throw new Error('Por favor, selecione uma imagem.');
                }

                const formData = new FormData();
                formData.append('nome', document.getElementById('nome').value);
                formData.append('descricao', document.getElementById('descricao').value);
                formData.append('preco', parseFloat(document.getElementById('preco').value));
                formData.append('quantidade', parseInt(document.getElementById('quantidade').value));
                formData.append('url', document.getElementById('url').value);
                formData.append('imagem', file);

                // Envia todos os dados em uma única requisição multipart
                await ApiService.postMultipart(CONFIG.ENDPOINTS.PRODUTOS, formData);
                
                UI.showSuccess('Produto cadastrado com sucesso!');
                UI.resetForm('produtoForm');
                if (previewContainer) previewContainer.style.display = 'none';
                if (previewImg) previewImg.src = '';
                
                // Recarrega a tabela se estiver na mesma página
                carregarProdutos();
            } catch (error) {
                UI.showError(error.message || 'Falha ao cadastrar produto.');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerText = originalBtnText;
            }
        });
    }

    const tabelaProdutos = document.getElementById('tabelaProdutos');
    if (tabelaProdutos) {
        tabelaProdutos.addEventListener('click', async (e) => {
            if (e.target.dataset.action === 'delete-produto') {
                const id = e.target.dataset.id;
                
                if (UI.confirmAction('Tem certeza que deseja excluir este produto?')) {
                    try {
                        await ApiService.delete(`${CONFIG.ENDPOINTS.PRODUTOS}/${id}`);
                        UI.removeElement(`row-produto-${id}`);
                        UI.removeElement(`row-${id}`);
                        UI.showSuccess('Produto excluído.');
                    } catch (error) {
                        UI.showError('Não foi possível excluir o produto.');
                    }
                }
            }
        });
    }

    // ==========================================
    // MÓDULO: GERENCIAMENTO DE USUÁRIOS (ADMIN)
    // ==========================================
    const tabelaUsuarios = document.getElementById('tabelaUsuarios');
    if (tabelaUsuarios) {
        tabelaUsuarios.addEventListener('click', async (e) => {
            if (e.target.dataset.action === 'delete-usuario') {
                const id = e.target.dataset.id;

                if (UI.confirmAction('Tem certeza que deseja excluir este usuário?')) {
                    try {
                        await ApiService.delete(`${CONFIG.ENDPOINTS.USUARIOS}/${id}`);
                        UI.removeElement(`row-${id}`);
                        UI.showSuccess('Usuário excluído com sucesso.');
                    } catch (error) {
                        UI.showError(error.message || 'Erro ao excluir usuário.');
                    }
                }
            }
        });
    }

    // ==========================================
    // FUNÇÕES AUXILIARES GERAIS
    // ==========================================
    async function carregarProdutos() {
        if (!document.getElementById('tabelaProdutos')) return;
        try {
            const produtos = await ApiService.get(CONFIG.ENDPOINTS.PRODUTOS);
            UI.renderProdutosTable(produtos);
        } catch (error) {
            console.error('Falha ao recarregar produtos:', error);
        }
    }

    async function atualizarContadorCarrinho() {
        const headerCartLink = document.getElementById('cartHeaderLink');
        if (!headerCartLink) return;
        try {
            const carrinho = await ApiService.get(CONFIG.ENDPOINTS.CARRINHO);
            headerCartLink.innerText = `Carrinho (${carrinho.quantidadeTotal || 0})`;
        } catch(e) {
            console.error('Falha ao atualizar contador do carrinho:', e);
        }
    }

});
