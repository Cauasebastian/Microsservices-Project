# Projeto de Microserviços Distribuídos

## Sobre o Projeto
Este projeto visa explorar e implementar uma arquitetura de microserviços usando diversas tecnologias modernas. Desenvolvido com o intuito educacional, este exemplo prático serve como um modelo para aplicativos corporativos baseados em microserviços.

## Funcionalidades
- **Cadastro e consulta de produtos:** Interação com o banco de dados através do product-service.
- **Gerenciamento de inventário:** Realiza operações de cadastro e consulta no inventory-service.
- **Processamento de pedidos:** O order-service verifica a disponibilidade dos produtos no inventory-service. Em caso de indisponibilidade, retorna uma mensagem de erro. Inclui implementações de circuit breakers e retry com resilience4j.
- **Autenticação e Autorização:** Utiliza Keycloak para gestão de autenticações e tokens.
- **Notificação Assíncrona:** Envio de eventos relacionados a pedidos através de um tópico de notificação para comunicação futura com o usuário.
## Tecnologias Utilizadas
- **Java:** Linguagem de programação para aplicativos corporativos.
- **Spring Boot:** Framework para desenvolvimento de APIs RESTful em Java.
- **Spring Data JPA / Hibernate:** Facilita a persistência de dados.
- **Spring Eureka, Spring Cloud Gateway:** Para descoberta de serviços e roteamento.
- **Grafana, Prometheus:** Ferramentas para monitoramento.
- **MySQL, MongoDB:** Sistemas de gerenciamento de banco de dados.
- **Zipkin, Kafka, Resilience4j:** Para tracing, mensageria e resiliência de serviços.
- **Docker:** Para a containerização de aplicações.
- **Maven:** Gerenciador de dependências e construção de projetos Java.
- **JUnit, Mockito, TestContainers:** Para testes unitários, mocking e testes de integração com Docker.
- **MongoExpress:** Interface web para o MongoDB.
- **Actuator:** Ferramenta para monitoramento e gerenciamento de aplicações Spring Boot.
- **Keycloak:** Para autenticação e autorização.

## Arquitetura do Sistema
Descrição geral de como os microserviços são organizados:

- **Product Service:** Gerencia informações dos produtos.
- **Inventory Service:** Responsável pelo estoque dos produtos.
- **Order Service:** Processa pedidos verificando a disponibilidade no Inventory Service.
- **Notification Service:** Envia notificações assíncronas para os usuários.
- **Discovery Service (Eureka):** Registra e descobre serviços.
- **API Gateway:** Interface única para todos os serviços, simplificando a interação do cliente com o backend.
- **Auth Service (Keycloak):** Gerencia autenticação e emissão de tokens.
- **Grafana, Prometheus, Zipkin,Actuator:** Ferramentas para monitoramento de métricas e rastreamento de logs.

## Comunicação entre Serviços
- **Síncrona:** Os serviços utilizam chamadas HTTP REST para comunicação direta, como entre Order Service e Inventory Service.
- **Assíncrona:** Kafka é utilizado para eventos que não requerem resposta imediata, como notificações de pedidos.
## Resiliência e Escalabilidade
- Implementação de **Circuit Breaker** e Retry Patterns com **Resilience4j** para manejar falhas nos serviços.
- Uso de **Spring Eureka** para descoberta de serviços, permitindo a escalabilidade horizontal sem configuração estática de IPs ou portas.

## Executando o Projeto com Docker
Com o Docker Compose instalado e configurado, execute:

````
docker-compose up --build
````
