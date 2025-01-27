function redireccionar(button) {
  const url = button.getAttribute("data-url-redireccion");
  console.log("Redirigiendo a:", url);
  window.location.href = url;
}