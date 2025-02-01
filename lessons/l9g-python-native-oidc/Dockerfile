# Dockerfile
FROM python:3.11-slim

# Arbeitsverzeichnis setzen
WORKDIR /work

# Abhängigkeiten kopieren und installieren
COPY requirements.txt .
RUN pip install --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

# Den Rest des Codes kopieren
COPY . .

# Startkommando: Das Python-Programm ausführen
CMD ["python", "app.py"]
