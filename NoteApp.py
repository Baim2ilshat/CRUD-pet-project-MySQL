
import mysql.connector

print("Hello, it's small app for notes")

# Connect to MySQL database
conn = mysql.connector.connect(
    host = "localhost",
    user = "root",
    password = "SQL_learning_Qz1",
    database = "notes_db"
)
print('Successfully connected to your data base!')
cursor = conn.cursor()

"""
# Create table (if not exists)
cursor.execute('''
CREATE TABLE IF NOT EXISTS notes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tag VARCHAR(50),
    note TEXT
)
''')"""

# Function to add a note
def add_note(tag, note):
    cursor.execute("INSERT INTO notes (tag, note) VALUES (%s, %s)", (tag, note))
    conn.commit()

# Function for showing all notes
def show_all():
    cursor.execute("SELECT * FROM notes")
    for row in cursor.fetchall():
        print(row)

# Function to search notes by tag
def search_by_tag(tag):
    cursor.execute("SELECT * FROM notes WHERE tag LIKE %s", f"%{tag}%")
    results = cursor.fetchall()
    if results:
        for row in cursor.fetchall():
            print(row)
    else:
        print('No matches found')

# Function to search by text
def search_in_notes(text):
    cursor.execute("SELECT * FROM notes WHERE note LIKE %s", f"%{text}%")
    results = cursor.fetchall()
    if results:
        for row in results:
            print(row)
    else:
        print('No matches found')

# Function to delete a note by ID
def delete_note_by_id(note_id):
    # First, check if the note exists
    cursor.execute("SELECT * FROM notes WHERE id = %s", (note_id,))
    note = cursor.fetchone()
    if note:
        # If it exists, delete it
        cursor.execute("DELETE FROM notes WHERE id = %s", (note_id,))
        conn.commit()
        print(f"✅ Note with ID {note_id} has been deleted.")
    else:
        print(f"⚠️ No note found with ID {note_id}.")


# Processing user's queries
print('Type e if you want close program otherwise type any key to start...')
qr = input()

while qr != 'e':
    print('What you want to do?')
    print('1) add new note')
    print('2) search notes by tag')
    print('3) search in notes by text')
    print('4) show all notes')
    print('5) delete note by id')
    qr = input()

    if qr == '1':
        data = input('Type here tag and text of note:').split()
        if len(data) > 1:
            user_tag = data[0]
            user_note = ' '.join(data[0:])
            add_note(user_tag, user_note)
        else:
            print('Invalid input. Please provide both a tag and a note.')

    elif qr == '2':
        user_tag = input('Type here by what tag you want to search:')
        search_by_tag(user_tag)

    elif qr == '3':
        user_text = input('Type here what text you what to search in notes:')
        search_in_notes(user_text)

    elif qr == '4':
        show_all()

    elif qr == '5':
        try:
            user_note_id = int(input('Type here id of note that you want to delete:'))
            delete_note_by_id(user_note_id)
        except ValueError:
            print('⚠️ Invalid input. Please enter a valid numeric id.')

    else:
        print('Invalid option. Please choose a valid number (1-5).')


# Close connection
print('Finishing...')
conn.close()