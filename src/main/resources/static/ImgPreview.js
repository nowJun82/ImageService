function previewImage() {
    const input = document.getElementById('imageInput');
    const preview = document.getElementById('preview');
    const imageSize = document.getElementById('imageSize');
    const file = input.files[0];

    if (file) {
        const reader = new FileReader();

        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.style.display = 'block';

            const img = new Image();
            img.src = e.target.result;
            img.onload = function() {
                imageSize.textContent = `${img.naturalWidth} * ${img.naturalHeight}`;
            };
        };

        reader.readAsDataURL(file);
    } else {
        preview.style.display = 'none';
        imageSize.textContent = '';
    }
}