
from flask import (
    Flask, render_template, request, redirect,
    url_for, session, abort, make_response
)
from functools import wraps
import csv
import io

app = Flask(__name__)
app.secret_key = "devops-student-app-secret-key"

# Demo credentials
USERS = {
    "admin": {"password": "admin123", "role": "admin"},
    "user": {"password": "user123", "role": "user"}
}

# Initial demo data: 25 students
students = [
    {
        "id": i,
        "name": f"Student {i:02d}",
        "course": ["Computer Science", "Information Technology",
                   "Electronics", "Mechanical", "Civil"][((i - 1) % 5)],
        "email": f"student{i:02d}@college.edu"
    }
    for i in range(1, 26)
]

next_student_id = 26


# -------------------------
# Authentication helpers
# -------------------------

def login_required(view):
    @wraps(view)
    def wrapped_view(*args, **kwargs):
        if "username" not in session:
            return redirect(url_for("login"))
        return view(*args, **kwargs)
    return wrapped_view


def admin_required(view):
    @wraps(view)
    def wrapped_view(*args, **kwargs):
        if "username" not in session:
            return redirect(url_for("login"))

        if session.get("role") != "admin":
            abort(403)

        return view(*args, **kwargs)
    return wrapped_view


# -------------------------
# Home and login
# -------------------------

@app.route("/")
def home():
    if "username" in session:
        return redirect(url_for("records"))
    return redirect(url_for("login"))


@app.route("/login", methods=["GET", "POST"])
def login():
    if request.method == "POST":
        username = request.form.get("username", "").strip()
        password = request.form.get("password", "")

        account = USERS.get(username)

        if account and account["password"] == password:
            session["username"] = username
            session["role"] = account["role"]
            return redirect(url_for("records"))

        return render_template(
            "login.html",
            error="Invalid username or password"
        )

    return render_template("login.html")


@app.route("/logout")
def logout():
    session.clear()
    return redirect(url_for("login"))


# -------------------------
# Student records and search
# -------------------------

@app.route("/records")
@login_required
def records():
    search = request.args.get("search", "").strip()

    filtered_students = [
        student for student in students
        if search.lower() in student["name"].lower()
    ]

    per_page = 10
    total = len(filtered_students)
    total_pages = max(1, (total + per_page - 1) // per_page)

    try:
        page = int(request.args.get("page", 1))
    except ValueError:
        page = 1

    page = max(1, min(page, total_pages))

    start = (page - 1) * per_page
    page_students = filtered_students[start:start + per_page]

    return render_template(
        "records.html",
        students=page_students,
        search=search,
        page=page,
        total_pages=total_pages
    )


# -------------------------
# Add a student
# -------------------------

@app.route("/records/add", methods=["GET", "POST"])
@admin_required
def add_record():
    global next_student_id

    if request.method == "POST":
        name = request.form.get("name", "").strip()
        course = request.form.get("course", "").strip()
        email = request.form.get("email", "").strip()

        if not name or not course or not email:
            return render_template(
                "form.html",
                title="Add Student",
                student={"name": name, "course": course, "email": email},
                error="Please fill in all fields."
            )

        students.append({
            "id": next_student_id,
            "name": name,
            "course": course,
            "email": email
        })

        next_student_id += 1
        return redirect(url_for("records"))

    return render_template(
        "form.html",
        title="Add Student",
        student={},
        error=None
    )


# -------------------------
# Edit a student
# -------------------------

@app.route("/records/<int:student_id>/edit", methods=["GET", "POST"])
@admin_required
def edit_record(student_id):
    student = next(
        (s for s in students if s["id"] == student_id), None
    )

    if student is None:
        abort(404)

    if request.method == "POST":
        name = request.form.get("name", "").strip()
        course = request.form.get("course", "").strip()
        email = request.form.get("email", "").strip()

        if not name or not course or not email:
            return render_template(
                "form.html",
                title="Edit Student",
                student=student,
                error="Please fill in all fields."
            )

        student["name"] = name
        student["course"] = course
        student["email"] = email

        return redirect(url_for("records"))

    return render_template(
        "form.html",
        title="Edit Student",
        student=student,
        error=None
    )


# -------------------------
# Delete a student
# -------------------------

@app.route("/records/<int:student_id>/delete", methods=["POST"])
@admin_required
def delete_record(student_id):
    student = next(
        (s for s in students if s["id"] == student_id), None
    )

    if student is None:
        abort(404)

    students.remove(student)
    return redirect(url_for("records"))


# -------------------------
# Admin panel
# -------------------------

@app.route("/admin")
@admin_required
def admin_panel():
    return render_template("admin.html", message=None)


@app.route("/admin/upload", methods=["POST"])
@admin_required
def upload_csv():
    file = request.files.get("file")

    if not file or not file.filename:
        return render_template(
            "admin.html", message="No file selected"
        )

    if not file.filename.lower().endswith(".csv"):
        return render_template(
            "admin.html", message="Only CSV files are allowed"
        )

    global next_student_id

    try:
        content = file.stream.read().decode("utf-8-sig")
        reader = csv.DictReader(io.StringIO(content))

        required = {"Name", "Course", "Email"}
        if not reader.fieldnames or not required.issubset(reader.fieldnames):
            return render_template(
                "admin.html",
                message="CSV must contain Name, Course, and Email columns"
            )

        new_students = []

        for row in reader:
            name = (row.get("Name") or "").strip()
            course = (row.get("Course") or "").strip()
            email = (row.get("Email") or "").strip()

            if name and course and email:
                new_students.append({
                    "id": next_student_id + len(new_students),
                    "name": name,
                    "course": course,
                    "email": email
                })

        students.extend(new_students)
        next_student_id += len(new_students)

        return render_template(
            "admin.html",
            message=f"Uploaded: {file.filename} ({len(new_students)} rows)"
        )

    except (UnicodeDecodeError, csv.Error):
        return render_template(
            "admin.html", message="Unable to read the CSV file"
        )


@app.route("/admin/report")
@admin_required
def download_report():
    output = io.StringIO()
    writer = csv.writer(output)

    writer.writerow(["ID", "Name", "Course", "Email"])

    for student in students:
        writer.writerow([
            student["id"],
            student["name"],
            student["course"],
            student["email"]
        ])

    response = make_response(output.getvalue())
    response.headers["Content-Disposition"] = (
        "attachment; filename=report.csv"
    )
    response.headers["Content-Type"] = "text/csv; charset=utf-8"

    return response


# -------------------------
# Error pages
# -------------------------

@app.errorhandler(403)
def forbidden(error):
    return render_template("403.html"), 403


@app.errorhandler(404)
def not_found(error):
    return "404 - Page Not Found", 404


if __name__ == "__main__":
    app.run(debug=True)