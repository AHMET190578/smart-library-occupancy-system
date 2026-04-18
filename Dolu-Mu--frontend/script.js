/* --- 1. DİL SÖZLÜĞÜ (Değişmedi) --- */
const translations = {
    tr: {
        settingsTitle: "AYARLAR",
        langLabel: "Sayfa Dili",
        kvkkLabel: "KVKK Aydınlatma Metni",
        darkModeLabel: "Karanlık Mod",
        pickerHeader: "Sayfa Dili Seçin"
    },
    en: {
        settingsTitle: "SETTINGS",
        langLabel: "Page Language",
        kvkkLabel: "Clarification Text (KVKK)",
        darkModeLabel: "Dark Mode",
        pickerHeader: "Select Page Language"
    }
};

/* --- 2. TEMA YÖNETİMİ --- */
window.toggleTheme = function() {
    const htmlElement = document.documentElement;
    const currentTheme = htmlElement.getAttribute('data-theme');
    const newTheme = (currentTheme === 'dark') ? 'light' : 'dark';
    
    htmlElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('app-theme', newTheme);
};

/* --- 3. DİL VE KVKK MODAL YÖNETİMİ --- */

window.toggleLanguageMenu = function() {
    const modal = document.getElementById('language-modal');
    const overlay = document.getElementById('modal-overlay');
    if (modal && overlay) {
        modal.classList.toggle('active');
        overlay.classList.toggle('active');
    }
};

window.toggleKVKKMenu = function() {
    const modal = document.getElementById('kvkk-modal');
    const overlay = document.getElementById('modal-overlay');
    if (modal && overlay) {
        modal.classList.toggle('active');
        overlay.classList.toggle('active');
    }
};

window.closeAllModals = function() {
    const languageModal = document.getElementById('language-modal');
    const kvkkModal = document.getElementById('kvkk-modal');
    const overlay = document.getElementById('modal-overlay');

    languageModal?.classList.remove('active');
    kvkkModal?.classList.remove('active');
    overlay?.classList.remove('active');
};

window.selectLanguage = function(lang) {
    // Radyo butonlarını güncelle
    document.getElementById('radio-tr')?.classList.remove('selected');
    document.getElementById('radio-en')?.classList.remove('selected');
    document.getElementById('radio-' + lang)?.classList.add('selected');

    applyTranslations(lang);
    localStorage.setItem('app-lang', lang);
    
    setTimeout(() => {
        closeAllModals();
    }, 400);
};

function applyTranslations(lang) {
    const text = translations[lang] || translations['tr'];
    
    const elements = {
        'set-title': text.settingsTitle,
        'set-lang-text': text.langLabel,
        'set-kvkk-text': text.kvkkLabel,
        'set-dark-text': text.darkModeLabel,
        'set-picker-header': text.pickerHeader
    };

    for (let id in elements) {
        const el = document.getElementById(id);
        if (el) el.innerText = elements[id];
    }
}

/* --- 4. BAŞLANGIÇ AYARLARI --- */
(function init() {
    const savedTheme = localStorage.getItem('app-theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);

    const savedLang = localStorage.getItem('app-lang') || 'tr';
    
    // DOM hazır olduğunda çalıştır
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            applyTranslations(savedLang);
            document.getElementById('radio-' + savedLang)?.classList.add('selected');
        });
    } else {
        applyTranslations(savedLang);
        document.getElementById('radio-' + savedLang)?.classList.add('selected');
    }
})();