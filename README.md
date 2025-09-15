# GraphPoet – A Graph-Based Poetry Generator

## 📖 Overview
**GraphPoet** is an implementation of the [MIT 6.005 (Software Construction)](https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-005-software-construction-fall-2016/) problem set.  
It uses **directed, weighted graphs** to model word affinities from a given text corpus and then generates poems by inserting “bridge words” between pairs of words in the input.  

For example, given the corpus:
This is a test of the Mugar Omni Theater sound system.

and the input:
Test the system.

GraphPoet produces:
Test of the system.


---

## ✨ Features
- Builds a **word-affinity graph** from any text corpus (case-insensitive, punctuation preserved).
- Vertices represent words; directed edges represent adjacency counts in the corpus.
- Inserts **bridge words** between input words when a two-edge path of maximum weight exists.
- Preserves **original casing** of input words; bridge words appear in lowercase.
- Includes two graph representations:
  - `ConcreteEdgesGraph` – explicit edge objects.
  - `ConcreteVerticesGraph` – vertex objects that track outgoing edges.
- Fully unit-tested with **JUnit**.

---

## 🛠 Tech Stack
- **Java 8+**
- **JUnit 4** for testing
- Standard Java collections (`HashMap`, `HashSet`, `List`, etc.)

---


