// ---------- Toast ----------
const toastEl = document.getElementById("toast");
let toastTimer;

function showToast(message, type = "success") {
  clearTimeout(toastTimer);
  toastEl.textContent = message;
  toastEl.className = `toast show ${type}`;
  toastTimer = setTimeout(() => {
    toastEl.className = "toast";
  }, 3000);
}

// ---------- Tabs ----------
document.querySelectorAll(".tab-btn").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".tab-btn").forEach((b) => b.classList.remove("active"));
    document.querySelectorAll(".tab-panel").forEach((p) => p.classList.remove("active"));
    btn.classList.add("active");
    document.getElementById(btn.dataset.tab).classList.add("active");
  });
});

// ---------- Modal helpers ----------
function openModal(id) {
  document.getElementById(id).classList.add("open");
}
function closeModal(id) {
  document.getElementById(id).classList.remove("open");
}
document.querySelectorAll("[data-close]").forEach((btn) => {
  btn.addEventListener("click", () => closeModal(btn.dataset.close));
});
document.querySelectorAll(".modal-overlay").forEach((overlay) => {
  overlay.addEventListener("click", (e) => {
    if (e.target === overlay) closeModal(overlay.id);
  });
});

function showFormErrors(elId, err) {
  const el = document.getElementById(elId);
  const messages = err.details?.length ? err.details : [err.message];
  el.innerHTML = messages.map((m) => `<div>• ${m}</div>`).join("");
}
function clearFormErrors(elId) {
  document.getElementById(elId).innerHTML = "";
}

// ---------- Confirm dialog ----------
function confirmAction(message, onConfirm) {
  document.getElementById("confirm-message").textContent = message;
  openModal("modal-confirm");
  const okBtn = document.getElementById("confirm-ok");
  const handler = async () => {
    okBtn.removeEventListener("click", handler);
    closeModal("modal-confirm");
    await onConfirm();
  };
  okBtn.addEventListener("click", handler);
}

// ---------- State ----------
let booksCache = [];
let membersCache = [];

// ---------- Books ----------
async function loadBooks() {
  const tbody = document.getElementById("books-tbody");
  try {
    booksCache = await BooksAPI.list();
    if (!booksCache.length) {
      tbody.innerHTML = `<tr class="empty-row"><td colspan="5">Nenhum livro cadastrado</td></tr>`;
      return;
    }
    tbody.innerHTML = booksCache
      .map(
        (b) => `
      <tr>
        <td>${escapeHtml(b.title)}</td>
        <td>${escapeHtml(b.author)}</td>
        <td>${escapeHtml(b.isbn)}</td>
        <td>${b.availableCopies} / ${b.totalCopies}</td>
        <td class="actions-cell">
          <button class="btn small" data-edit-book="${b.id}">Editar</button>
          <button class="btn small danger" data-delete-book="${b.id}">Excluir</button>
        </td>
      </tr>`
      )
      .join("");
  } catch (err) {
    showToast(err.message, "error");
  }
}

document.getElementById("btn-new-book").addEventListener("click", () => {
  document.getElementById("form-book").reset();
  document.getElementById("book-id").value = "";
  document.getElementById("book-modal-title").textContent = "Novo livro";
  clearFormErrors("book-errors");
  openModal("modal-book");
});

document.getElementById("books-tbody").addEventListener("click", (e) => {
  const editId = e.target.dataset.editBook;
  const delId = e.target.dataset.deleteBook;

  if (editId) {
    const book = booksCache.find((b) => String(b.id) === editId);
    document.getElementById("book-id").value = book.id;
    document.getElementById("book-title").value = book.title;
    document.getElementById("book-author").value = book.author;
    document.getElementById("book-isbn").value = book.isbn;
    document.getElementById("book-totalCopies").value = book.totalCopies;
    document.getElementById("book-modal-title").textContent = "Editar livro";
    clearFormErrors("book-errors");
    openModal("modal-book");
  }

  if (delId) {
    const book = booksCache.find((b) => String(b.id) === delId);
    confirmAction(`Excluir o livro "${book.title}"?`, async () => {
      try {
        await BooksAPI.remove(delId);
        showToast("Livro excluído com sucesso");
        loadBooks();
      } catch (err) {
        showToast(err.message, "error");
      }
    });
  }
});

document.getElementById("form-book").addEventListener("submit", async (e) => {
  e.preventDefault();
  clearFormErrors("book-errors");
  const id = document.getElementById("book-id").value;
  const dto = {
    title: document.getElementById("book-title").value.trim(),
    author: document.getElementById("book-author").value.trim(),
    isbn: document.getElementById("book-isbn").value.trim(),
    totalCopies: Number(document.getElementById("book-totalCopies").value),
  };
  try {
    if (id) {
      await BooksAPI.update(id, dto);
      showToast("Livro atualizado com sucesso");
    } else {
      await BooksAPI.create(dto);
      showToast("Livro cadastrado com sucesso");
    }
    closeModal("modal-book");
    loadBooks();
  } catch (err) {
    showFormErrors("book-errors", err);
  }
});

// ---------- Members ----------
async function loadMembers() {
  const tbody = document.getElementById("members-tbody");
  try {
    membersCache = await MembersAPI.list();
    if (!membersCache.length) {
      tbody.innerHTML = `<tr class="empty-row"><td colspan="3">Nenhum membro cadastrado</td></tr>`;
      return;
    }
    tbody.innerHTML = membersCache
      .map(
        (m) => `
      <tr>
        <td>${escapeHtml(m.name)}</td>
        <td>${escapeHtml(m.email)}</td>
        <td class="actions-cell">
          <button class="btn small" data-edit-member="${m.id}">Editar</button>
          <button class="btn small danger" data-delete-member="${m.id}">Excluir</button>
        </td>
      </tr>`
      )
      .join("");
  } catch (err) {
    showToast(err.message, "error");
  }
}

document.getElementById("btn-new-member").addEventListener("click", () => {
  document.getElementById("form-member").reset();
  document.getElementById("member-id").value = "";
  document.getElementById("member-modal-title").textContent = "Novo membro";
  clearFormErrors("member-errors");
  openModal("modal-member");
});

document.getElementById("members-tbody").addEventListener("click", (e) => {
  const editId = e.target.dataset.editMember;
  const delId = e.target.dataset.deleteMember;

  if (editId) {
    const member = membersCache.find((m) => String(m.id) === editId);
    document.getElementById("member-id").value = member.id;
    document.getElementById("member-name").value = member.name;
    document.getElementById("member-email").value = member.email;
    document.getElementById("member-modal-title").textContent = "Editar membro";
    clearFormErrors("member-errors");
    openModal("modal-member");
  }

  if (delId) {
    const member = membersCache.find((m) => String(m.id) === delId);
    confirmAction(`Excluir o membro "${member.name}"?`, async () => {
      try {
        await MembersAPI.remove(delId);
        showToast("Membro excluído com sucesso");
        loadMembers();
      } catch (err) {
        showToast(err.message, "error");
      }
    });
  }
});

document.getElementById("form-member").addEventListener("submit", async (e) => {
  e.preventDefault();
  clearFormErrors("member-errors");
  const id = document.getElementById("member-id").value;
  const dto = {
    name: document.getElementById("member-name").value.trim(),
    email: document.getElementById("member-email").value.trim(),
  };
  try {
    if (id) {
      await MembersAPI.update(id, dto);
      showToast("Membro atualizado com sucesso");
    } else {
      await MembersAPI.create(dto);
      showToast("Membro cadastrado com sucesso");
    }
    closeModal("modal-member");
    loadMembers();
  } catch (err) {
    showFormErrors("member-errors", err);
  }
});

// ---------- Loans ----------
function statusBadge(status) {
  const map = { ATIVO: "ativo", DEVOLVIDO: "devolvido", ATRASADO: "atrasado" };
  return `<span class="badge ${map[status] || ""}">${status}</span>`;
}

async function loadLoans() {
  const tbody = document.getElementById("loans-tbody");
  try {
    const loans = await LoansAPI.list();
    if (!loans.length) {
      tbody.innerHTML = `<tr class="empty-row"><td colspan="7">Nenhum empréstimo registrado</td></tr>`;
      return;
    }
    tbody.innerHTML = loans
      .map(
        (l) => `
      <tr>
        <td>${escapeHtml(l.bookTitle)}</td>
        <td>${escapeHtml(l.memberName)}</td>
        <td>${l.loanDate ?? "-"}</td>
        <td>${l.dueDate ?? "-"}</td>
        <td>${l.returnDate ?? "-"}</td>
        <td>${statusBadge(l.status)}</td>
        <td class="actions-cell">
          ${l.status !== "DEVOLVIDO" ? `<button class="btn small" data-return-loan="${l.id}">Devolver</button>` : ""}
        </td>
      </tr>`
      )
      .join("");
  } catch (err) {
    showToast(err.message, "error");
  }
}

document.getElementById("btn-new-loan").addEventListener("click", async () => {
  clearFormErrors("loan-errors");
  try {
    if (!booksCache.length) booksCache = await BooksAPI.list();
    if (!membersCache.length) membersCache = await MembersAPI.list();

    const bookSelect = document.getElementById("loan-bookId");
    const memberSelect = document.getElementById("loan-memberId");

    bookSelect.innerHTML = booksCache
      .map((b) => `<option value="${b.id}">${escapeHtml(b.title)} (${b.availableCopies} disp.)</option>`)
      .join("");
    memberSelect.innerHTML = membersCache
      .map((m) => `<option value="${m.id}">${escapeHtml(m.name)}</option>`)
      .join("");

    openModal("modal-loan");
  } catch (err) {
    showToast(err.message, "error");
  }
});

document.getElementById("loans-tbody").addEventListener("click", (e) => {
  const loanId = e.target.dataset.returnLoan;
  if (loanId) {
    confirmAction("Registrar a devolução deste livro?", async () => {
      try {
        await LoansAPI.returnBook(loanId);
        showToast("Devolução registrada com sucesso");
        loadLoans();
        loadBooks();
      } catch (err) {
        showToast(err.message, "error");
      }
    });
  }
});

document.getElementById("form-loan").addEventListener("submit", async (e) => {
  e.preventDefault();
  clearFormErrors("loan-errors");
  const dto = {
    bookId: Number(document.getElementById("loan-bookId").value),
    memberId: Number(document.getElementById("loan-memberId").value),
  };
  try {
    await LoansAPI.borrow(dto);
    showToast("Empréstimo registrado com sucesso");
    closeModal("modal-loan");
    loadLoans();
    loadBooks();
  } catch (err) {
    showFormErrors("loan-errors", err);
  }
});

// ---------- Utils ----------
function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

// ---------- Init ----------
loadBooks();
loadMembers();
loadLoans();
