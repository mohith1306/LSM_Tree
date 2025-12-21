# Mini LSM-Tree Based Key-Value Storage Engine

## Overview
This project is a simple implementation of a **Log Structured Merge Tree (LSM-Tree)** based key-value storage engine written in **Java**.  
It demonstrates how modern databases handle high write throughput using in-memory structures and immutable disk files.

This project is intended for **academic learning and system design understanding**.

---

## Key Features
- Fast writes using in-memory MemTable
- Sorted MemTable using TreeMap
- Write-Ahead Logging (WAL) for durability
- Immutable SSTables stored on disk
- Logical deletes using tombstones
- Basic crash recovery mechanism

---

## Architecture

```text
mini-lsm/
 ├── data/
 ├── LICENSE
 ├── Main.class
 ├── Main.java
 ├── MemTable.class
 ├── MemTable.java
 ├── README.md
 ├── StorageEngine.class
 └── StorageEngine.java
```

---

## Workflow

### Write Operation (PUT / DELETE)
1. Append operation to Write-Ahead Log
2. Insert data into MemTable
3. Flush MemTable to SSTable when size limit is reached

### Read Operation (GET)
1. Check MemTable
2. Check SSTables (newest to oldest)
3. Return the latest value or NULL

### Delete Operation
- Uses tombstones for logical deletion
- Actual removal happens during compaction

### Crash Recovery
- WAL is replayed on startup
- MemTable is rebuilt from log entries

---


## Supported Commands


PUT <key> <value>
GET <key>
DELETE <key>
EXIT



### Example

PUT name Mohith
GET name
Mohith
DELETE name
GET name
NULL



---

## How to Run
1. Clone the repository
git clone https://github.com/mohith1306/LSM_Tree.git
2. Open the project in VS Code or any Java IDE
3. Run `Main.java`

---

## Technologies Used
- Java
- Java Collections (TreeMap)
- File I/O
- Command Line Interface

---

## Concepts Covered
- LSM-Tree architecture
- Write-Ahead Logging
- MemTable and SSTables
- Tombstone-based deletes
- Disk compaction
- Crash recovery

---

## Future Enhancements
- Bloom filters for faster reads
- Multi-level compaction
- Range queries
- Background compaction threads
- Configuration using properties file

---

## Author
Mohith T  
Computer Science Student

---

## License
This project is for educational purposes.
