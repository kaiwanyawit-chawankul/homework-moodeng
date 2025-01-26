# Cassandra

## Comparing MS SQL vs. Cassandra

Both MS SQL and Cassandra are database management systems, but they serve different purposes and have distinct architectures. Here's a comparison:

| Feature | MS SQL | Cassandra |
|---|---|---|
| **Schema** | Schema | Keyspace |
|  | Defines the structure of the database, including tables, columns, data types, and constraints. | A namespace that groups tables (called "column families" in older Cassandra versions). It's similar to a database in SQL. |
| **Command to list** | Use SQL Server Management Studio (SSMS) or execute `SELECT name FROM sys.databases;` in T-SQL. | `DESCRIBE keyspaces;` in CQL (Cassandra Query Language). |
| **Primary Database Model** | Relational DBMS | Wide Column Store (NoSQL) |
| **Data Structure** | Tables with rows and columns. Relationships between tables are defined using foreign keys. | Tables with rows and columns, but with a flexible schema. Data is organized into partitions and within partitions, data is clustered by columns. |
| **ACID Properties** | Fully supports ACID (Atomicity, Consistency, Isolation, Durability) transactions. | Provides tunable consistency levels, but does not fully adhere to ACID principles in the traditional sense. Focuses on availability and partition tolerance (CAP theorem). |
| **Scalability** | Scales vertically (by adding more resources to a single server) and horizontally (using techniques like replication and partitioning, but can be complex). | Designed for horizontal scalability. Easily scales by adding more nodes to the cluster. |
| **Data Consistency** | Strong consistency. Guarantees that all reads will see the most recent write. | Tunable consistency. Offers various levels of consistency, allowing users to choose between strong consistency and higher availability. |
| **Query Language** | T-SQL (Transact-SQL) | CQL (Cassandra Query Language), which is similar to SQL but with some differences. |
| **Use Cases** | Applications requiring complex transactions, data integrity, and strong consistency, such as financial systems, ERP, and CRM. | Applications requiring high availability, fault tolerance, and massive scalability, such as social media platforms, IoT, and time-series data. |

**Key Differences Summarized:**

*   **Data Model:** MS SQL is relational, while Cassandra is NoSQL (wide column store).
*   **Consistency:** MS SQL prioritizes strong consistency, while Cassandra offers tunable consistency.
*   **Scalability:** MS SQL scales vertically and horizontally (with some complexity), while Cassandra is designed for easy horizontal scaling.
*   **Transactions:** MS SQL fully supports ACID transactions, while Cassandra does not provide traditional ACID transactions.

**In essence:**

*   Choose **MS SQL** when you need complex transactions, strong consistency, and a well-defined schema.
*   Choose **Cassandra** when you need high availability, fault tolerance, massive scalability, and can tolerate eventual consistency.

## to connect to CLI

docker exec -it cassandra cqlsh
