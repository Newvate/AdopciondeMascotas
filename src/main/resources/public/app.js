const petGrid = document.querySelector('#pet-grid');
const searchInput = document.querySelector('#pet-search');
const noResults = document.querySelector('#no-results');
const resultsCount = document.querySelector('#results-count');
const clearSearch = document.querySelector('#clear-search');
const sortSelect = document.querySelector('#pet-sort');
const breedFilter = document.querySelector('#breed-filter');
const locationFilter = document.querySelector('#location-filter');
const ageFilter = document.querySelector('#age-filter');
const sizeFilter = document.querySelector('#size-filter');
const favoritesOnly = document.querySelector('#favorites-only');
const filterButtons = [...document.querySelectorAll('.filter-chip')];
const favoriteIds = new Set(JSON.parse(localStorage.getItem('hogar-fiel-favoritos') || '[]').map(String));
let pets = [];
let selectedFilter = 'all';

const normalize = (value = '') => value.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLocaleLowerCase('es');
const escapeHtml = (value = '') => String(value).replace(/[&<>"']/g, (char) => ({
  '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
})[char]);

function saveFavorites() {
  localStorage.setItem('hogar-fiel-favoritos', JSON.stringify([...favoriteIds]));
}

function fillOptions(select, values, firstLabel) {
  const current = select.value;
  const unique = [...new Set(values.filter(Boolean))].sort((a, b) => a.localeCompare(b, 'es'));
  select.replaceChildren(new Option(firstLabel, ''), ...unique.map((value) => new Option(value, value)));
  if (unique.includes(current)) select.value = current;
}

function ageGroup(months) {
  if (months < 12) return 'baby';
  if (months <= 36) return 'young';
  return 'adult';
}

function renderPets() {
  const query = normalize(searchInput.value.trim());
  const shown = pets.filter((pet) => {
    const matchesType = selectedFilter === 'all' || normalize(pet.tipo) === selectedFilter;
    const matchesSearch = !query || normalize(`${pet.nombre} ${pet.raza} ${pet.ubicacion}`).includes(query);
    const matchesBreed = !breedFilter.value || pet.raza === breedFilter.value;
    const matchesLocation = !locationFilter.value || pet.ubicacion === locationFilter.value;
    const matchesAge = !ageFilter.value || ageGroup(Number(pet.edadMeses || 0)) === ageFilter.value;
    const matchesSize = !sizeFilter.value || pet.tamano === sizeFilter.value;
    const matchesFavorite = !favoritesOnly.checked || favoriteIds.has(String(pet.id));
    return matchesType && matchesSearch && matchesBreed && matchesLocation && matchesAge && matchesSize && matchesFavorite;
  });
  if (sortSelect.value === 'name') shown.sort((a, b) => a.nombre.localeCompare(b.nombre, 'es'));
  if (sortSelect.value === 'young') shown.sort((a, b) => Number(a.edadMeses) - Number(b.edadMeses));

  petGrid.innerHTML = shown.map((pet, index) => {
    const id = String(pet.id);
    const saved = favoriteIds.has(id);
    const type = normalize(pet.tipo);
    const tagClass = ['tag-lime', 'tag-pink', 'tag-orange', 'tag-purple'][index % 4];
    const pronoun = normalize(pet.sexo) === 'hembra' ? 'Conocerla' : 'Conocerlo';
    return `<article class="pet-card">
      <div class="pet-photo"><img src="${escapeHtml(pet.imagen)}" alt="${escapeHtml(`${pet.nombre}, ${pet.tipo.toLowerCase()}`)}" loading="lazy"><span class="pet-tag ${tagClass}">EN ADOPCIÓN</span><button class="favorite" type="button" aria-label="${saved ? 'Quitar de favoritos' : 'Guardar en favoritos'} a ${escapeHtml(pet.nombre)}" aria-pressed="${saved}" data-favorite="${escapeHtml(id)}">${saved ? '♥' : '♡'}</button></div>
      <div class="pet-info"><div class="pet-title"><h3>${escapeHtml(pet.nombre)}</h3><span class="pet-age">${escapeHtml(pet.edad)}</span></div><span class="pet-code">Código ${escapeHtml(pet.codigo)}</span><p>${escapeHtml(pet.raza)} <span>·</span> ${escapeHtml(pet.sexo)}</p><p class="pet-description">${escapeHtml(pet.descripcion || '')}</p><div class="pet-footer"><span class="pet-location">⌖ ${escapeHtml(pet.ubicacion)}</span><button class="meet-button" type="button" data-meet="${escapeHtml(id)}">${pronoun} <span>↗</span></button></div></div>
    </article>`;
  }).join('');
  noResults.hidden = shown.length !== 0;
  resultsCount.textContent = `${shown.length} ${shown.length === 1 ? 'mascota' : 'mascotas'}`;
  clearSearch.hidden = !searchInput.value;
}

async function loadPets() {
  try {
    const response = await fetch('/api/mascotas');
    if (!response.ok) throw new Error('No se pudo cargar el catálogo.');
    pets = await response.json();
    fillOptions(breedFilter, pets.map((pet) => pet.raza), 'Todas las razas');
    fillOptions(locationFilter, pets.map((pet) => pet.ubicacion), 'Todas las ubicaciones');
    fillOptions(sizeFilter, pets.map((pet) => pet.tamano), 'Todos los tamaños');
    filterButtons[0].innerHTML = `Todos <span>${String(pets.length).padStart(2, '0')}</span>`;
    renderPets();
  } catch (error) {
    resultsCount.textContent = 'No se pudo cargar el catálogo';
    petGrid.innerHTML = '';
  }
}

filterButtons.forEach((button) => button.addEventListener('click', () => {
  selectedFilter = button.dataset.filter;
  filterButtons.forEach((item) => {
    const active = item === button;
    item.classList.toggle('selected', active);
    item.setAttribute('aria-pressed', String(active));
  });
  renderPets();
}));
[searchInput, breedFilter, locationFilter, ageFilter, sizeFilter, favoritesOnly, sortSelect].forEach((control) => {
  control.addEventListener('input', renderPets);
  control.addEventListener('change', renderPets);
});
clearSearch.addEventListener('click', () => {
  searchInput.value = '';
  renderPets();
  searchInput.focus();
});

petGrid.addEventListener('click', (event) => {
  const favoriteButton = event.target.closest('[data-favorite]');
  if (favoriteButton) {
    const id = favoriteButton.dataset.favorite;
    favoriteIds.has(id) ? favoriteIds.delete(id) : favoriteIds.add(id);
    saveFavorites();
    renderPets();
    return;
  }
  const meetButton = event.target.closest('[data-meet]');
  if (meetButton) {
    const pet = pets.find((item) => String(item.id) === meetButton.dataset.meet);
    if (!pet) return;
    document.querySelector('#selected-pet').textContent = pet.nombre;
    document.querySelector('#form-success').hidden = true;
    document.querySelector('#adoption-form').hidden = false;
    document.querySelector('#adoption-form').dataset.pet = pet.nombre;
    document.querySelector('#adoption-dialog').showModal();
  }
});

const dialog = document.querySelector('#adoption-dialog');
document.querySelector('.dialog-close').addEventListener('click', () => dialog.close());
dialog.addEventListener('click', (event) => { if (event.target === dialog) dialog.close(); });
document.querySelector('#adoption-form').addEventListener('submit', async (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const data = Object.fromEntries(new FormData(form));
  data.mensaje = `Interés en adoptar a ${form.dataset.pet}. ${data.mensaje}`;
  const button = form.querySelector('button[type="submit"]');
  button.disabled = true;
  try {
    const response = await fetch('/api/contacto', {
      method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data)
    });
    if (!response.ok) throw new Error('No se pudo enviar la solicitud.');
    form.hidden = true;
    document.querySelector('#form-success').hidden = false;
  } catch (error) {
    button.textContent = 'No se pudo enviar. Intenta de nuevo';
  } finally {
    button.disabled = false;
  }
});

const menuToggle = document.querySelector('.menu-toggle');
const nav = document.querySelector('#main-nav');
menuToggle.addEventListener('click', () => {
  const open = menuToggle.getAttribute('aria-expanded') !== 'true';
  menuToggle.setAttribute('aria-expanded', String(open));
  nav.classList.toggle('open', open);
});
nav.querySelectorAll('a').forEach((link) => link.addEventListener('click', () => {
  nav.classList.remove('open');
  menuToggle.setAttribute('aria-expanded', 'false');
}));

loadPets();
