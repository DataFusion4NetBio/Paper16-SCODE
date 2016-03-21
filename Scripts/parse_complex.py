__author__ = 'Sarah'

dict = {}
with open("CYC2008_complex_limited.txt") as f:
    content = f.readlines()
    for line in content:
        line_split = line.split("\t")
        protein = line_split[1].rstrip('\n')
        complex_name = line_split[2].rstrip('\n')
        if(complex_name in dict):
            dict[complex_name].append(protein)
        else:
            dict[complex_name] = [protein]

with open("CYC2008complexes.txt", "a") as myfile:
    counter = 1
    for complex in dict:
        myfile.write(str(counter) + "\t" + complex + "\t" + " ".join(dict[complex]) + "\n")
        counter = counter + 1

with open("proteins.txt", "a") as myfile:
    for complex in dict:
        for protein in dict[complex]:
            myfile.write(protein + "\n")