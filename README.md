
# Aplicativo de Gerenciamento de Clínica - Java (Console)

Este projeto acadêmico simula uma aplicação de gerenciamento de médicos, pacientes e consultas, com interação em modo console e estrutura baseada em orientação a objetos.

## 📂 Estrutura de Arquivos

```
├── Main.java
├── AdminView.java
├── DoctorView.java
├── PatientView.java
├── Appointment.java
├── Doctor.java
├── Patient.java
├── doctors_clean.csv
├── patients.csv
├── appointments.csv
└── credentials.properties
```

## 🧠 Funcionalidades

### 👨‍⚕️ Painel do Médico
- Listagem de médicos por nome (ordenada)
- Visualização de pacientes atendidos com nome e CPF formatado
- Consultas em determinado período com exibição paginada
- Pacientes inativos há X meses

### 🧍 Painel do Paciente
- Lista todos os médicos com quem já se consultou
- Mostra consultas passadas com um médico específico
- Mostra consultas futuras agendadas

### 🧑‍💼 Painel do Administrador
- Cadastro de médicos e pacientes
- Login com autenticação via `credentials.properties`
- Atualização da base a partir dos arquivos CSV

## 🔐 Segurança
- Credenciais separadas em `credentials.properties`
- Leitura protegida por `try-with-resources`

## 📊 Formatos dos Arquivos CSV

### doctors_clean.csv
```
Nome,Codigo
Alexandre Fernandes,12345
```

### patients.csv
```
Nome,CPF
Anna Petersson,12345678901
```

### appointments.csv
```
Data,Horario,CPF_Paciente,CRM_Medico
2025-08-01,10:00,12345678901,12345
```

## ▶️ Como Executar

1. Compile todos os arquivos `.java`:
```
javac *.java
```

2. Execute a aplicação:
```
java Main
```

## 🚀 Próximos Passos

- Persistência com JDBC (banco de dados relacional)
- Interface gráfica com Java Swing ou JavaFX
- Exportação de relatórios PDF
- Login com criptografia e logs de acesso

---

Desenvolvido por Jafte Carneiro Fagundes da Silva para fins acadêmicos no curso de Ciência da Computação - PUCPR.
