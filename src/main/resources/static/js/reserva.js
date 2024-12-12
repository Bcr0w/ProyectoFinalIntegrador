let medicos = [];
// Fechas y horas habilitadas (puedes cargarlas dinámicamente desde el backend)
let fechasConHoras = {};
let medicoAlmacenado = "";
let fecha = "";
let hora = "";

const daysContainer = document.getElementById("days-container");
const hoursContainer = document.getElementById("hours-container");
const currentMonthSpan = document.getElementById("current-month");
let currentDate = new Date();

function obtenerMedicos() {
    fetch('/api/v1/medico/lista', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            medicos = data; // Almacena los médicos en el arreglo
            console.log('Médicos obtenidos:', medicos);
            mostrarMedicos(); // Llama a una función para mostrarlos si es necesario
        })
        .catch(error => {
            console.error('Error al obtener la lista de médicos:', error);
        });
}

function mostrarMedicos(selectElement) {
    const value = selectElement.value; // Obtiene el valor seleccionado
    console.log("Especialidad seleccionada:", value); // Muestra el valor seleccionado en la consola
    const selectMedico = document.getElementById('medico');

    // Limpiar las opciones existentes (excepto la predeterminada)
    const opcionesExistentes = Array.from(selectMedico.options);
    opcionesExistentes.forEach((opcion) => {
        if (opcion.value !== "") {
            opcion.remove();
        }
    });

    // Crear y agregar las nuevas opciones
    medicos.forEach(medico => {
        if (value == medico.idEspecialidad) {
            const option = document.createElement('option');
            option.value = medico.id; // Usa el ID del médico como valor
            option.textContent = `Dr./Dra. ${medico.nombre} ${medico.apellidos}`; // Texto visible en el dropdown
            selectMedico.appendChild(option);
        }

    });
}


// Función para obtener las horas desde el servidor
function obtenerHoras(idMedico) {
    return new Promise((resolve, reject) => {
        fetch(`/api/v1/horario/obtener?idMedico=${idMedico}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la respuesta de la API');
                }
                return response.json(); // Parsear la respuesta como JSON
            })
            .then(data => {
                // Verifica si la respuesta contiene las fechas con horas
                console.log("Datos obtenidos:", data);

                if (data && typeof data === 'object') {
                    fechasConHoras = data;
                    console.log("fechasConHoras actualizadas:", fechasConHoras);
                    resolve(); // Resuelve la promesa una vez que los datos se actualizan
                } else {
                    console.error('Los datos obtenidos no son válidos:', data);
                    reject('Datos no válidos');
                }
            })
            .catch(error => {
                console.error('Hubo un error con la solicitud Fetch:', error);
                reject(error); // Rechaza la promesa si ocurre un error
            });
    });
}

const monthNames = [
    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
];



// Render days dynamically
function renderDays() {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    // Days in the current month
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const firstDayOfMonth = new Date(year, month, 1).getDay();

    // Clear existing days
    daysContainer.innerHTML = "";
    // Verificar que fechasConHoras contiene los datos
    console.log(fechasConHoras); // Depuración

    // Add days to the container
    for (let day = 1; day <= daysInMonth; day++) {
        const dayElement = document.createElement("div");
        dayElement.classList.add("day");

        const dayName = ["Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"][(firstDayOfMonth + day - 1) % 7];
        const formattedDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        dayElement.innerText = `${dayName} ${day}`;
        dayElement.dataset.date = formattedDate;

        // Check if the day is enabled
        if (fechasConHoras[formattedDate]) {
            dayElement.classList.add("enabled");
            dayElement.addEventListener("click", () => {
                fecha = formattedDate;
                showHours(formattedDate);
            });
        } else {
            dayElement.classList.add("disabled");
        }

        daysContainer.appendChild(dayElement);
    }
}

// Show hours for the selected day
function showHours(date) {
    hoursContainer.innerHTML = ""; // Clear previous hours
    const hours = fechasConHoras[date] || [];

    if (hours.length > 0) {
        hours.forEach((hour) => {
            const hourElement = document.createElement("div");
            hourElement.classList.add("hour");
            hourElement.innerText = hour;
            hourElement.addEventListener("click", () => {
                hora = hour;
                alert(`Hora seleccionada: ${hour}`);
            });

            hoursContainer.appendChild(hourElement);
        });
    } else {
        const noHoursMessage = document.createElement("p");
        noHoursMessage.innerText = "No hay horas disponibles para esta fecha.";
        hoursContainer.appendChild(noHoursMessage);
    }
}

// Navigate months
document.getElementById("prev-month").addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    updateCalendar();
});

document.getElementById("next-month").addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    updateCalendar();
});

function updateCalendar() {
    currentMonthSpan.innerText = `${monthNames[currentDate.getMonth()]}`;
    renderDays();
}

// CAMBIAR DE PASO 1 y 2
// Manejo de pasos
function mostrarPaso(paso) {
    document.querySelectorAll('.paso').forEach(p => p.classList.add('d-none'));
    document.getElementById(`paso${paso}`).classList.remove('d-none');
}


// Capturar el valor cuando se haga clic en el botón
function cargarHorasMedico() {
    const selectElement = document.getElementById('medico');
    const medicoSeleccionado = selectElement.value; // Obtiene el valor de la opción seleccionada
    //Dato medico
    medicoAlmacenado = medicoSeleccionado;


    if (medicoSeleccionado) {
        obtenerHoras(medicoSeleccionado).then(() => {
            updateCalendar(); // Ejecutar solo después de que los datos se actualicen
        });
    } else {
        console.log("Por favor, selecciona un médico antes de continuar.");
        event.stopImmediatePropagation();
    }
}

//funciones para la animacion de carga para el pago
function mostrarCargando() {
    document.getElementById("loading-modal").style.display = "flex";
}

function ocultarCargando() {
    document.getElementById("loading-modal").style.display = "none";
}


// Obtén el botón de envío y el formulario
const form = document.getElementById('myForm');
const submitBtn = document.getElementById('submitBtn');




document.getElementById('myForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Detener el envío predeterminado del formulario

    // Obtener los valores de los campos del formulario
    const data = {
        idMedico: medicoAlmacenado, // medico
        fechaConfirmada: fecha, // fecha
        horaConfirmada: hora // hora
    };
    mostrarCargando();
    // Realizar el fetch
    fetch('/paypal/payment/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json', // Especificar que los datos se envían en JSON
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al crear el pago');
            }
            return response.json(); // Convertir la respuesta a JSON
        })
        .then(data => {
            if (data.redirectUrl) {
                // Redirigir al usuario a PayPal
                window.location.href = data.redirectUrl;
            } else {
                alert('Error: No se encontró la URL de aprobación');
            }
        })
        .catch(error => {
            ocultarCargando();
            console.error('Error:', error);
            alert('Hubo un problema al procesar el pago');
        });
});
//funcion para verificicar la seleccion de especialidad y medico
function validarFormulario(event) {
    // Obtiene los elementos select
    const especialidadSelect = document.getElementById('especialidad');
    const medicoSelect = document.getElementById('medico');

    // Obtiene los valores seleccionados
    const especialidadSeleccionada = especialidadSelect.value;
    const medicoSeleccionado = medicoSelect.value;

    // Validación de ambos campos
    if (especialidadSeleccionada === "" || especialidadSeleccionada === null) {
        alert("Por favor, selecciona una especialidad antes de continuar.");
        event.preventDefault(); // Evita la ejecución de la siguiente función
        return; // Detiene la ejecución si no se seleccionó una especialidad
    }

    if (medicoSeleccionado === "" || medicoSeleccionado === null) {
        alert("Por favor, selecciona un médico antes de continuar.");
        event.preventDefault(); // Evita la ejecución de la siguiente función
        return; // Detiene la ejecución si no se seleccionó un médico
    }

    // Si todo está bien, ejecuta las funciones
    cargarHorasMedico();
    mostrarPaso(2);
}

// Initialize calendar
obtenerMedicos();