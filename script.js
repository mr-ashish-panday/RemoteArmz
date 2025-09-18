// Smooth scroll (for internal links)
document.addEventListener('click', function (e) {
  const target = e.target.closest('a[href^="#"]');
  if (!target) return;
  const href = target.getAttribute('href');
  if (href.length > 1) {
    const el = document.querySelector(href);
    if (el) {
      e.preventDefault();
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
});

// Intersection Observer for reveal animations
const observer = new IntersectionObserver(
  (entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        observer.unobserve(entry.target);
      }
    });
  },
  { threshold: 0.2 }
);

// Observe reveal elements, each step card, and logos
document.querySelectorAll('.reveal, .client-logos img').forEach((el, i) => {
  el.style.transitionDelay = `${(i % 5) * 0.2}s`;
  observer.observe(el);
});

// Replace skeleton class once images load
document.querySelectorAll('img.skeleton').forEach((img) => {
  if (img.complete) img.classList.remove('skeleton');
  img.addEventListener('load', () => img.classList.remove('skeleton'));
  img.addEventListener('error', () => img.classList.remove('skeleton'));
});

// Performance: defer non-critical tasks
window.addEventListener('load', () => {
  // Potential place to lazy-init third-party embeds (Calendly inline, etc.)
  const header = document.querySelector('.site-header');
  const onScroll = () => {
    if (!header) return;
    if (window.scrollY > 12) header.classList.add('solid');
    else header.classList.remove('solid');
  };
  onScroll();
  window.addEventListener('scroll', onScroll, { passive: true });

  // Active nav link highlighting
  const sections = ['brand-title', 'case-study', 'process', 'pricing'];
  const navLinks = document.querySelectorAll('.nav-links a[href^="#"]');
  const highlight = () => {
    let activeId = '';
    sections.forEach((id) => {
      const el = document.getElementById(id);
      if (!el) return;
      const rect = el.getBoundingClientRect();
      if (rect.top <= 120 && rect.bottom >= 120) activeId = id;
    });
    navLinks.forEach((a) => {
      const href = a.getAttribute('href') || '';
      const id = href.replace('#', '');
      if (id && id === activeId) a.classList.add('active');
      else a.classList.remove('active');
    });
  };
  highlight();
  window.addEventListener('scroll', highlight, { passive: true });

  // GA4 CTA click tracking (graceful no-op if gtag missing)
  document.querySelectorAll('.cta-track').forEach((el) => {
    el.addEventListener('click', () => {
      const label = el.getAttribute('data-cta') || 'cta';
      if (typeof gtag === 'function') {
        gtag('event', 'cta_click', { event_category: 'CTA', event_label: label });
      }
    });
  });

  // Mobile menu toggle
  const menuButton = document.getElementById('mobileMenuButton');
  const mobileMenu = document.getElementById('mobileMenu');
  if (menuButton && mobileMenu) {
    const closeMenu = () => {
      menuButton.setAttribute('aria-expanded', 'false');
      mobileMenu.hidden = true;
      document.body.style.overflow = '';
    };
    const openMenu = () => {
      menuButton.setAttribute('aria-expanded', 'true');
      mobileMenu.hidden = false;
      document.body.style.overflow = 'hidden';
    };
    menuButton.addEventListener('click', () => {
      const expanded = menuButton.getAttribute('aria-expanded') === 'true';
      if (expanded) closeMenu(); else openMenu();
    });
    mobileMenu.querySelectorAll('a').forEach((a) => {
      a.addEventListener('click', () => closeMenu());
    });
  }
});


