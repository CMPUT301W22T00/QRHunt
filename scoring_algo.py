# python implementation of the scoring algorithm used here: 
# https://eclass.srv.ualberta.ca/mod/page/view.php?id=5825439#:~:text=proposed%20scoring%20system%3A

# re = regular expression module for python.
# see https://en.wikipedia.org/wiki/Regular_expression and regex101.com
import re


def score(hash_as_string: str):
    # prefixed r(aw) before the quotes to tell python to ignore escape sequences like \n
    # the parens indicate a capturing group
    # ((\w)(\2+)) this #1 capturing group will contain all the repeated characters in the end
    #  (\w)       this #2 capturing group will match one single word character ie [a-zA-Z0-9_]
    #      (\2+)  this #3 capturing group will match whatever our #2 capturing group captured one or more times
    # play around with this regex: https://regex101.com/r/zIf1iG/1
    pattern = r'((\w)(\2+))'
    # matches will contain a list of tuples, each string in the tuple corresponds to a capturing group
    # for examples, on of the items in the list could be ("bbb", "b", "bb") which corresponds to capturing group 1,2,3
    matches = re.findall(pattern, hash_as_string)

    total = 0
    for match in matches:
        # eg  "bbb"          "b"         "bb"
        full_group, second_group, third_group = match

        # the formula for value is <hex value of letter> to the power of (n-1), where n is number of repetitions
        if full_group[0] == '0':
            # 0 gets special treatment as the value 20
            val = 20
        else:
            # by specifying the 2nd argument of base, this interprets full_group[0] as a hex value
            val = int(full_group[0], 16)

        # ** = to the power of
        total += val ** (len(full_group) - 1)

    return total


def score_one_liner(h: str):
    return sum((20 if f[0] == '0' else int(f[0], 16)) ** (len(f) - 1) for f, *_ in re.findall(r'((\w)(\2+))', h))
