#!/usr/bin/env python3
"""
Dependency resolver for the TodoAI project.

Layered Architecture
--------------------
ui → viewmodel → usecase → repository → data-local (Room)
                            ├── agent.tools (pure Kotlin, no DB deps)
                            └── agent / parser   (pure Kotlin, no DB deps)

Rules of thumb
--------------
1. Never import a Room DAO, entity, or context into a domain-level class.
2. Domain / agent classes must remain platform-agnostic (no Android APIs).
3. ViewModels receive use-cases and agent tools via @Inject constructor
   (Hilt delivers them).
4. Screens are thin Composables: collect StateFlow, call VM fn, display.
"""

def print_rules() -> None:
    print(__doc__)

if __name__ == "__main__":
    print_rules()
