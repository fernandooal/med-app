
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
---
# Registro de Melhorias na Aplicação Java de Gerenciamento de Clínica

**Data de atualização:** 20/04/2025

Este documento compila todas as melhorias aplicadas na aplicação em Java que gerencia médicos, pacientes e consultas, com foco em qualidade de código, segurança, desempenho e padronização.

---

## ✅ 1. Uniformização de Tipos de Dados

- **Código do médico (CRM)** foi padronizado como `String` em todas as classes (`Doctor`, `Appointment`, `DoctorView`, `AdminView`, `Main`) para manter compatibilidade com arquivos CSV (`doctors_clean.csv`) e evitar problemas de formatação (`#####/PR`).
- O CSV antigo `doctors.csv` foi substituído por `doctors_clean.csv`, contendo apenas `Nome,Codigo`.

---

## ✅ 2. Segurança na Autenticação

- As credenciais de login deixaram de ser hardcoded.
- Criado arquivo `credentials.properties` com o seguinte formato:
```properties
username=admin
password=admin1234
```
- O método `login()` em `AdminView.java` passou a carregar dinamicamente essas credenciais com `Properties` e `FileInputStream`.

---

## ✅ 3. Otimização de Desempenho

- Uso de `HashMap<String, Doctor>` para mapeamento rápido de médicos por código no `Main.java`.
- Implementada **paginação** nos módulos `DoctorView.java` e `PatientView.java`:
    - Permite exibir listas longas (consultas, pacientes) em blocos de 10 itens com navegação por página.

---

## ✅ 4. Tratamento Refinado de Erros

- Substituição de `catch (Exception)` por exceções específicas:
    - `NumberFormatException` para entrada numérica inválida.
    - `IOException` para erros de leitura de arquivo.
- Validação de entrada do usuário com `regex`, ex:
    - CPF: `\d11`
    - Código do médico: `\d+`
- Mensagens de erro descritivas e localização precisa dos erros no CSV.

---

## ✅ 5. Gerenciamento de Recursos

- Uso consistente de **`try-with-resources`** para leitura de arquivos, entrada de usuário e escrita de CSV:
    - `Scanner`, `PrintWriter`, `FileInputStream` etc.
- Elimina vazamentos de recurso e garante fechamento automático de arquivos.

---

## ✅ 6. Padronização e Consistência de Arquivos

- Todas as operações de leitura e escrita de médicos foram consolidadas para `doctors_clean.csv`.
- Métodos afetados:
    - `registerDoctor()`
    - `updateDoctorsFromCSV()`
    - `Doctor.loadFromCSV()`
    - Leitura no `Main.java`

---

## ✅ 7. Separação de Responsabilidades (Clean Code)

- O método `Appointment.loadFromCSV()` agora delega o parsing de linha para `parseLine(String)`, isolando validações e formatação.
- Facilita manutenção e debugging do módulo de agendamento.

---

## 📌 Conclusão

A aplicação foi significativamente aprimorada em termos de:

- Confiabilidade dos dados
- Segurança da autenticação
- Qualidade do código
- Escalabilidade para grandes volumes
- Consistência no tratamento de médicos e consultas

A arquitetura atual permite fácil expansão, integração com persistência em banco de dados ou frontend, e está preparada para evoluir com novos requisitos.





---



## 🚀 Próximos Passos

- Interface gráfica com Java Swing ou JavaFX
- Exportação de relatórios PDF
- Login com criptografia e logs de acesso

---

Desenvolvido por:

- Angelo Piovezan Jorgeto
- Fernando Alonso P. da Silva
- Jafte Carneiro Fagundes da Silva
- Renato Pestana Gouveia

Para fins acadêmicos no curso de Ciência da Computação - PUCPR.

