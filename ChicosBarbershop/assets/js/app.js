(() => {
  const toggle = document.querySelector('.nav-toggle');
  const nav = document.querySelector('.main-nav');
  if (toggle && nav) {
    toggle.addEventListener('click', () => {
      const isOpen = nav.classList.toggle('open');
      toggle.setAttribute('aria-expanded', String(isOpen));
    });
  }

  const bookingForm = document.querySelector('.booking-form');
  if (bookingForm) {
    const service = document.getElementById('service_id');
    const barber = document.getElementById('barber_id');
    const date = document.getElementById('appointment_date');
    const time = document.getElementById('start_time');
    const help = document.getElementById('slot-help');
    let requestNumber = 0;

    const loadSlots = async () => {
      const serviceId = service.value;
      const barberId = barber.value;
      const selectedDate = date.value;
      time.innerHTML = '<option value="">Loading available times…</option>';
      time.disabled = true;
      if (!serviceId || !barberId || !selectedDate) {
        time.innerHTML = '<option value="">Choose a service, barber and date first</option>';
        help.textContent = 'Available times will appear here.';
        return;
      }
      const requestId = ++requestNumber;
      help.textContent = 'Checking the schedule…';
      try {
        const url = new URL(bookingForm.dataset.slotsUrl, window.location.href);
        url.searchParams.set('service_id', serviceId);
        url.searchParams.set('barber_id', barberId);
        url.searchParams.set('date', selectedDate);
        const response = await fetch(url, { headers: { 'Accept': 'application/json' } });
        if (!response.ok) throw new Error('Could not load available times.');
        const result = await response.json();
        if (requestId !== requestNumber) return;
        time.innerHTML = '';
        if (!result.slots || result.slots.length === 0) {
          time.innerHTML = '<option value="">No times available</option>';
          help.textContent = 'No available times for this selection. Try another date or barber.';
          return;
        }
        const placeholder = document.createElement('option');
        placeholder.value = '';
        placeholder.textContent = 'Select an available time';
        time.appendChild(placeholder);
        result.slots.forEach(slot => {
          const option = document.createElement('option');
          option.value = slot;
          const [hours, minutes] = slot.split(':').map(Number);
          const hour12 = hours % 12 || 12;
          const suffix = hours >= 12 ? 'PM' : 'AM';
          option.textContent = `${hour12}:${String(minutes).padStart(2, '0')} ${suffix}`;
          time.appendChild(option);
        });
        time.disabled = false;
        help.textContent = `${result.slots.length} available time${result.slots.length === 1 ? '' : 's'} found.`;
      } catch (error) {
        time.innerHTML = '<option value="">Unable to load times</option>';
        help.textContent = 'Please refresh the page and try again.';
      }
    };

    [service, barber, date].forEach(control => control.addEventListener('change', loadSlots));
    bookingForm.addEventListener('submit', event => {
      if (!service.value || !barber.value || !date.value || !time.value) {
        event.preventDefault();
        help.textContent = 'Please choose a service, barber, date and available time.';
        time.focus();
      }
    });
  }
})();
