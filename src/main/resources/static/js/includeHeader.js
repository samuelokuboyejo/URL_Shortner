async function loadHeader() {
    try {
        const res = await fetch('/header.html');
        if (!res.ok) throw new Error('Failed to load header');
        const headerHTML = await res.text();
        document.getElementById('headerContainer').innerHTML = headerHTML;
    } catch (err) {
        console.error(err);
    }
}

window.addEventListener('DOMContentLoaded', loadHeader);
