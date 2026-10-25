const filterButtons = document.querySelectorAll('.filter-chip');
const cards = [...document.querySelectorAll('.pet-card')];
const searchInput = document.querySelector('#pet-search');
const noResults = document.querySelector('#no-results');
let selectedFilter = 'all';

function updatePets() {
  const query = searchInput.value.trim().toLocaleLowerCase('es');
  let visible = 0;
  cards.forEach((card) => {
    const matchesType = selectedFilter === 'all' || card.dataset.type === selectedFilter;
    const matchesName = card.dataset.name.includes(query);
    card.hidden = !(matchesType && matchesName);
    if (!card.hidden) visible += 1;
  });
  noResults.hidden = visible !== 0;
}

filterButtons.forEach((button) => button.addEventListener('click', () => {
  selectedFilter = button.dataset.filter;
  filterButtons.forEach((item) => {
    const active = item === button;
    item.classList.toggle('selected', active);
    item.setAttribute('aria-pressed', String(active));
  });
  updatePets();
}));
searchInput.addEventListener('input', updatePets);

document.querySelectorAll('.favorite').forEach((button) => button.addEventListener('click', () => {
  const saved = button.getAttribute('aria-pressed') !== 'true';
  button.setAttribute('aria-pressed', String(saved));
  button.textContent = saved ? '♥' : '♡';
}));

const dialog = document.querySelector('#adoption-dialog');
const selectedPet = document.querySelector('#selected-pet');
document.querySelectorAll('.meet-button').forEach((button) => button.addEventListener('click', () => {
  selectedPet.textContent = button.dataset.pet;
  document.querySelector('#form-success').hidden = true;
  document.querySelector('#adoption-form').hidden = false;
  dialog.showModal();
}));
document.querySelector('.dialog-close').addEventListener('click', () => dialog.close());
dialog.addEventListener('click', (event) => {
  if (event.target === dialog) dialog.close();
});
document.querySelector('#adoption-form').addEventListener('submit', (event) => {
  event.preventDefault();
  event.currentTarget.hidden = true;
  document.querySelector('#form-success').hidden = false;
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
