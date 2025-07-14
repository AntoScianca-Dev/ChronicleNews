setTimeout(() => {
    const flashMessage = document.querySelector(".flash-message");
    if (flashMessage) {
        flashMessage.style.display = "none";
    }
}, 5000);

// dark mode
const btnDarkMode = document.querySelector('#theme-toggle');
const iconDarkMode= document.querySelector('#theme-icon');
const isDarkMode = localStorage.getItem("theme") === "dark";
if(isDarkMode){
    document.body.classList.add("dark");
};
btnDarkMode.addEventListener("click", () => {
    document.body.classList.toggle("dark");
    if(document.body.classList.contains("dark")){
        localStorage.setItem("theme", "dark");
        iconDarkMode.classList.add('fa-moon');
        iconDarkMode.classList.remove('fa-sun');
    } else {
        localStorage.setItem("theme", "light");
        iconDarkMode.classList.remove('fa-moon');
        iconDarkMode.classList.add('fa-sun');
    }
});

// torna indietro
function goBack() {
    window.history.back();
}

// anteprima immagine caricata
let imginput = document.querySelector('#files');
let previewContainer = document.querySelector('#previewContainer');
let previewArticleImages = document.querySelector('#previewArticleImages');
let preview = document.querySelector('#preview')

if(imginput){
    imginput.addEventListener('change', () => {
        
        // da eseguire solo nella pagina edit
        if(preview){
            preview.classList.remove('d-none');
            previewArticleImages.innerHTML = ''; // Nella pagina edit se vengono caricate le nuove immagini non sono più visualizzare le vecchie immagini
        }
        
        previewContainer.innerHTML = ''; // Svuota anteprime precedenti
        Array.from(imginput.files).forEach(file => {
            if (file.type.startsWith('image/')) {
                const reader = new FileReader();
    
                reader.onload = (e) => {
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    img.classList.add('img-thumbnail', 'rounded-5', 'img-preview-article');
                    previewContainer.appendChild(img);
                };
    
                reader.readAsDataURL(file);
            }
        });
    });
}

// counter animato per calcolo articoli dashboard review/writer 
document.addEventListener("DOMContentLoaded", function () {

    const counters = document.querySelectorAll("h4 span");

    counters.forEach(counter => {
        const value = parseInt(counter.textContent);
        if (isNaN(value) || value === 0) return;

        let current = 0;
        const duration = 20000; 
        const steps = 100;    
        const increment = Math.ceil(value / steps);
        const delay = duration / steps;

        counter.textContent = "0";

        const updateCount = () => {
            current += increment;
            if (current >= value) {
                counter.textContent = value;
            } else {
                counter.textContent = current;
                setTimeout(updateCount, delay);
            }
        };

        setTimeout(updateCount, delay);
    });
});

// reaction detail
document.addEventListener("DOMContentLoaded", function () {
    const box = document.getElementById("reaction-box");
    const articleId = box?.dataset.articleId;

    if (!articleId) return; // safety check

    const selectedP = document.getElementById("selected-reaction");

    const storageKey = `reaction:article:${articleId}`;

    const savedReaction = localStorage.getItem(storageKey);
    if (savedReaction) {
        highlightReaction(savedReaction);
    }

    box.querySelectorAll(".reaction-btn").forEach(btn => {
        btn.addEventListener("click", function () {
            const selected = this.dataset.reaction;
            localStorage.setItem(storageKey, selected);
            highlightReaction(selected);
        });
    });

    function highlightReaction(selected) {
        box.querySelectorAll(".reaction-btn").forEach(btn => {
            btn.style.opacity = (btn.dataset.reaction === selected) ? "1.0" : "0.1";
        });
    }
});