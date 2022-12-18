import random

# Klasse für die Karten
class Card:
  def __init__(self, suit, value):
    self.suit = suit
    self.value = value
  
  def __repr__(self):
    return f"{self.value} of {self.suit}"

# Klasse für das Solitaire-Spiel
class Solitaire:
  def __init__(self):
    # Erstelle ein Deck mit 52 Karten
    self.deck = [Card(suit, value) for suit in ["Hearts", "Diamonds", "Spades", "Clubs"]
                                     for value in ["Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"]]
    random.shuffle(self.deck)
    
    # Erstelle die 7 Stapel, auf denen das Spiel gespielt wird
    self.stacks = [[] for _ in range(7)]
    
    # Verteile die Karten auf die 7 Stapel
    for i in range(7):
      for j in range(i+1):
        self.stacks[i].append(self.deck.pop())
      
      # Kehre die oberste Karte um, damit sie sichtbar ist
      self.stacks[i][-1].face_up = True
  
  def play(self):
    # Spielschleife
    while True:
      # Zeige den aktuellen Zustand des Spiels an
      self.display_stacks()
      
      # Frage den Spieler, welche Karte er verschieben möchte
      source_stack = int(input("From which stack do you want to move a card? "))
      source_index = int(input("Which card do you want to move (enter the index)? "))
      dest_stack = int(input("To which stack do you want to move the card? "))
      
      # Verschiebe die Karte
      card = self.stacks[source_stack].pop(source_index)
      self.stacks[dest_stack].append(card)
      
      # Kehre die oberste Karte um, wenn der Stapel leer war
      if not self.stacks[dest_stack]:
        self.stacks[dest_stack][-1].face_up = True
      
      # Prüfe, ob das Spiel gewonnen wurde
      if self.win():
        print("You won!")
        break
  
  def display_stacks(self):
    # Zeige den aktuellen Zustand der Stapel an
    for i, stack in enumerate(self.stacks):
      print(f"Stack {i+1}: {stack}")
